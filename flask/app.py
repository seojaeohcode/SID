import os
import torch
import onnxruntime
from torchvision import transforms
from PIL import Image
import numpy as np

# 이미지 변환 설정 (이미지 비율을 1:1로 강제 조정)
normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                 std=[0.229, 0.224, 0.225])

# 비율을 유지하지 않고 1:1로 크기를 조정
transform = transforms.Compose([
    transforms.Resize((256, 256)),  # 256x256으로 강제로 크기 조정 (비율을 유지하지 않음)
    transforms.ToTensor(),
    normalize,
])

# 모델을 로드합니다.
model_path = f"NIADerma_4cls.onnx"
ort_session = onnxruntime.InferenceSession(model_path)

def to_numpy(tensor):
    return tensor.detach().cpu().numpy() if tensor.requires_grad else tensor.cpu().numpy()

def predict_image(image_path):
    # 이미지 로드 및 전처리
    img = Image.open(image_path).convert("RGB")
    img = transform(img).unsqueeze(0)  # 배치 차원을 추가합니다.

    # ONNX 모델에 입력할 수 있도록 NumPy 배열로 변환합니다.
    ort_inputs = {ort_session.get_inputs()[0].name: to_numpy(img)}
    
    # 모델 예측 수행
    ort_outs = ort_session.run(None, ort_inputs)
    output = torch.tensor(ort_outs[0])
    
    # 예측 결과의 확률로 변환
    probabilities = torch.nn.Softmax(dim=1)(output)
    
    # 가장 높은 확률의 인덱스를 가져와 클래스 예측을 수행
    predicted_class = torch.argmax(probabilities, dim=1).item()
    
    return predicted_class, probabilities

# 이미지 데이터 경로 및 규칙 설정
base_path = "C:\\Users\\HB\\Downloads\\TS\\1. 디지털카메라\\"
start_index = 1
end_index = 1100  # 데이터의 마지막 인덱스 설정

results = {}

# 각 폴더 및 이미지 파일에 대해 반복
for idx in range(start_index, end_index + 1):
    folder_name = f"{idx:04d}"
    image_path = f"{base_path}{folder_name}\\{folder_name}_01_F.jpg"

    # 해당 폴더와 이미지 파일이 존재하는지 확인
    if os.path.exists(image_path):
        try:
            predicted_class, probabilities = predict_image(image_path)
            results[image_path] = {
                'predicted_class': predicted_class,
                'probabilities': probabilities.tolist()  # 확률 값들을 리스트 형식으로 저장
            }
        except Exception as e:
            print(f"Error processing image {image_path}: {e}")
    else:
        print(f"Image or folder not found: {image_path}")

# 결과 출력
for image_path, result in results.items():
    print(f"Image: {image_path}")
    print(f"Predicted Class: {result['predicted_class']}")
    print(f"Probabilities: {result['probabilities']}\n")




# from flask import Flask, request, jsonify
# import os
# import numpy as np
# import onnxruntime as ort
# from PIL import Image
# import cv2

# app = Flask(__name__)


# class NIADermaDataset(object):
#     def __init__(self, root, transforms=None):
#         self.root = root
#         self.transforms = transforms
#         self.imgs = list(sorted(os.listdir(os.path.join(root, "image"))))
#         self.labels = list(sorted(os.listdir(os.path.join(root, "label"))))

#     def __getitem__(self, idx):
#         img_path = os.path.join(self.root, "image", self.imgs[idx])
#         label_path = os.path.join(self.root, "label", self.labels[idx])
#         img = Image.open(img_path).convert("RGB")
#         label = open(label_path, "r").readline()
#         label = int(label)

#         if self.transforms is not None:
#             img = self.transforms(img)

#         return img, label

#     def __len__(self):
#         return len(self.imgs)


# # ONNX 모델 로드
# def load_model(model_path):
#     model = ort.InferenceSession(model_path)
#     return model


# def predict(model, img):
#     # 이미지 전처리
#     img = np.array(img)  # 이미지를 NumPy 배열로 변환
#     img = cv2.resize(img, (256, 256))  # 이미지 크기 조정 (256x256)
#     img = img.astype(np.float32) / 255.0  # [0, 255]에서 [0, 1]로 변환

#     # 정규화
#     mean = np.array([0.485, 0.456, 0.406], dtype=np.float32)  # 평균값
#     std = np.array([0.229, 0.224, 0.225], dtype=np.float32)  # 표준편차
#     img = (img - mean) / std  # 정규화

#     img = img.transpose(2, 0, 1)  # HWC에서 CHW로 변환
#     img = np.expand_dims(img, axis=0)  # 배치 차원 추가

#     ort_inputs = {model.get_inputs()[0].name: img}
#     outputs = model.run(None, ort_inputs)

#     return outputs


# # 모델 로드
# model_path = r"D:\SID\flask\NIADerma_4cls.onnx"
# model = load_model(model_path)


# @app.route("/")
# def hello():
#     return "Hello World!"


# @app.route("/diagnosis", methods=["POST"])
# def diagnosis():
#     try:
#         # 클라이언트로부터 이미지 파일 받기
#         if "image" not in request.files:
#             return "No image part", 400

#         file = request.files["image"]

#         if file.filename == "":
#             return "No selected file", 400

#         img = Image.open(file.stream).convert("RGB")
#         img = img.resize((256, 256))  # 모델이 요구하는 크기로 조정
#         prediction = predict(model, img)

#         # 예측 결과가 NumPy 배열인지 확인
#         if isinstance(prediction, np.ndarray):
#             prediction_list = prediction.tolist()  # NumPy 배열을 리스트로 변환
#         elif isinstance(prediction, list):
#             prediction_list = prediction  # 이미 리스트인 경우
#         else:
#             return "Unexpected prediction format", 500

#         # 결과를 문자열로 반환
#         return str(prediction_list)

#     except Exception as e:
#         return str(e), 500


# if __name__ == "__main__":
#     app.run()
