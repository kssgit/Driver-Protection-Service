from tensorflow.keras.models import load_model
import tensorflow as tf
import cv2
import dlib
import numpy as np
from imutils import face_utils

# gpu 메모리 최대로 잡는 것을 방지
gpus = tf.config.experimental.list_physical_devices('GPU')
if gpus:
    try:
        # Currently, memory growth needs to be the same across GPUs
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
        logical_gpus = tf.config.experimental.list_logical_devices('GPU')
        print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
    except RuntimeError as e:
        # Memory growth must be set before GPUs have been initialized
        print(e)

model = load_model('C:/Users/s_csmscox/jupyterSave/eye_blink/eye_blink_CNN.h5')

detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor('shape_predictor_68_face_landmarks.dat')

img_size = (32, 32)


def crop_eye(img, eye_points):
    x1, y1 = np.amin(eye_points, axis=0)
    x2, y2 = np.amax(eye_points, axis=0)
    cx, cy = (x1 + x2) / 2, (y1 + y2) / 2

    w = (x2 - x1) * 1.2
    h = w * img_size[1] / img_size[0]

    margin_x, margin_y = w / 2, h / 2

    min_x, min_y = int(cx - margin_x), int(cy - margin_y)
    max_x, max_y = int(cx + margin_x), int(cy + margin_y)

    eye_rect = np.rint([min_x, min_y, max_x, max_y]).astype(np.int)

    eye_img = img[eye_rect[1]:eye_rect[3], eye_rect[0]:eye_rect[2]]

    return eye_img, eye_rect


img = cv2.imread('face3.jpg')

faces = detector(img)

for face in faces:
    shapes = predictor(img, face)
    shapes = face_utils.shape_to_np(shapes)

    eye_img_l, eye_rect_l = crop_eye(img, eye_points=shapes[36:42])
    eye_img_r, eye_rect_r = crop_eye(img, eye_points=shapes[42:48])

    eye_img_l = cv2.resize(eye_img_l, dsize=img_size)
    eye_img_r = cv2.resize(eye_img_r, dsize=img_size)

    # 왼쪽 눈
    eye_input_l = eye_img_l.copy().reshape((1, img_size[1], img_size[0], 3)).astype(np.float32)
    eye_input_l = eye_input_l / 255

    pred_l = model.predict(eye_input_l)
    pred_l = np.argmax(pred_l)

    # 오른쪽 눈
    eye_input_r = eye_img_r.copy().reshape((1, img_size[1], img_size[0], 3)).astype(np.float32)
    eye_input_r = eye_input_r / 255

    pred_r = model.predict(eye_input_r)
    pred_r = np.argmax(pred_r)

    cv2.rectangle(img, pt1=tuple(eye_rect_l[0:2]), pt2=tuple(eye_rect_l[2:4]), color=(255, 255, 255), thickness=2)
    cv2.rectangle(img, pt1=tuple(eye_rect_r[0:2]), pt2=tuple(eye_rect_r[2:4]), color=(255, 255, 255), thickness=2)

    cv2.putText(img, str(pred_l), tuple(eye_rect_l[0:2]), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
    cv2.putText(img, str(pred_r), tuple(eye_rect_r[0:2]), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)

    cv2.imshow('image', img)

    cv2.waitKey(0)
    cv2.destroyAllWindows()

    # 두 눈 다 감은 경우 졸음으로 예측
    if pred_l == 0 and pred_r == 0:
        print("예측 : 졸음")
    else:
        print("예측 : 안졸음")
