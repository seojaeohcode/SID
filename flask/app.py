from flask import Flask, request, jsonify
import os
import numpy as np
import onnxruntime as ort
from PIL import Image
import cv2

app = Flask(__name__)


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


def predict(model, img):
    # 이미지 전처리
    img = np.array(img)  # 이미지를 NumPy 배열로 변환
    img = cv2.resize(img, (256, 256))  # 이미지 크기 조정 (256x256)
    img = img.astype(np.float32) / 255.0  # [0, 255]에서 [0, 1]로 변환

    # 정규화
    mean = np.array([0.485, 0.456, 0.406], dtype=np.float32)  # 평균값
    std = np.array([0.229, 0.224, 0.225], dtype=np.float32)  # 표준편차
    img = (img - mean) / std  # 정규화

    img = img.transpose(2, 0, 1)  # HWC에서 CHW로 변환
    img = np.expand_dims(img, axis=0)  # 배치 차원 추가

    ort_inputs = {model.get_inputs()[0].name: img}
    outputs = model.run(None, ort_inputs)

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
