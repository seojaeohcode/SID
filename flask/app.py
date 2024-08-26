from flask import Flask, request, jsonify
import os
import numpy as np
import onnxruntime as ort
from PIL import Image

app = Flask(__name__)


# NIADermaDataset 클래스 (이전 코드와 동일)
class NIADermaDataset(object):
    def __init__(self, root, transforms=None):
        self.root = root
        self.transforms = transforms
        self.imgs = list(sorted(os.listdir(os.path.join(root, "image"))))
        self.labels = list(sorted(os.listdir(os.path.join(root, "label"))))

    def __getitem__(self, idx):
        img_path = os.path.join(self.root, "image", self.imgs[idx])
        label_path = os.path.join(self.root, "label", self.labels[idx])
        img = Image.open(img_path).convert("RGB")
        label = open(label_path, "r").readline()
        label = int(label)

        if self.transforms is not None:
            img = self.transforms(img)

        return img, label

    def __len__(self):
        return len(self.imgs)


# ONNX 모델 로드
def load_model(model_path):
    model = ort.InferenceSession(model_path)
    return model


# 예측 함수
def predict(model, img):
    img = np.array(img).astype(np.float32)
    img = img.transpose(2, 0, 1)  # HWC to CHW
    img = np.expand_dims(img, axis=0)  # Add batch dimension

    outputs = model.run(None, {model.get_inputs()[0].name: img})
    return outputs


# 모델 로드
model_path = r"D:\SID\flask\NIADerma_4cls.onnx"
model = load_model(model_path)


@app.route("/")
def hello():
    return "Hello World!"


@app.route("/diagnosis", methods=["POST"])
def diagnosis():
    try:
        # 클라이언트로부터 이미지 파일 받기
        if "image" not in request.files:
            return "No image part", 400

        file = request.files["image"]

        if file.filename == "":
            return "No selected file", 400

        img = Image.open(file.stream).convert("RGB")
        img = img.resize((256, 256))  # 모델이 요구하는 크기로 조정
        prediction = predict(model, img)

        # 예측 결과가 NumPy 배열인지 확인
        if isinstance(prediction, np.ndarray):
            prediction_list = prediction.tolist()  # NumPy 배열을 리스트로 변환
        elif isinstance(prediction, list):
            prediction_list = prediction  # 이미 리스트인 경우
        else:
            return "Unexpected prediction format", 500

        # 결과를 문자열로 반환
        return str(prediction_list)

    except Exception as e:
        return str(e), 500


if __name__ == "__main__":
    app.run()
