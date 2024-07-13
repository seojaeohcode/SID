import cv2
import numpy as np
from sklearn.mixture import GaussianMixture
import matplotlib.pyplot as plt
from google.colab import drive

# Google Drive 마운트
print("Google Drive 마운트 시작")
drive.mount('/content/gdrive')
print("Google Drive 마운트 완료")

def extract_skin_regions(image):
    # Convert to HSV color space
    hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

    # Define skin HSV ranges
    skin_h_lower = 0
    skin_h_upper = 17  # Increase upper limit
    skin_s_lower = 15  # Decrease lower limit
    skin_s_upper = 170
    skin_v_lower = 0  # Decrease lower limit
    skin_v_upper = 255  # Increase upper limit

    # Create a mask for skin pixels based on HSV ranges
    HSV_mask = cv2.inRange(hsv_image, (skin_h_lower, skin_s_lower, skin_v_lower), (skin_h_upper, skin_s_upper, skin_v_upper))
    HSV_mask = cv2.morphologyEx(HSV_mask, cv2.MORPH_OPEN, np.ones((3,3), np.uint8))

    # Convert to YCrCb color space
    YCrCb_image = cv2.cvtColor(image, cv2.COLOR_BGR2YCrCb)

    # Define skin YCrCb ranges
    skin_y_lower = 0
    skin_y_upper = 255
    skin_cr_lower = 135
    skin_cr_upper = 180
    skin_cb_lower = 85
    skin_cb_upper = 135

    # Create a mask for skin pixels based on YCrCb ranges
    YCrCb_mask = cv2.inRange(YCrCb_image, (skin_y_lower, skin_cr_lower, skin_cb_lower), (skin_y_upper, skin_cr_upper, skin_cb_upper))
    YCrCb_mask = cv2.morphologyEx(YCrCb_mask, cv2.MORPH_OPEN, np.ones((3,3), np.uint8))

    # Merge skin detection (YCrCb and HSV)
    global_mask = cv2.bitwise_and(YCrCb_mask, HSV_mask)
    global_mask = cv2.medianBlur(global_mask, 3)
    global_mask = cv2.morphologyEx(global_mask, cv2.MORPH_OPEN, np.ones((4,4), np.uint8))

    # Further refine skin regions
    skin_regions = refine_skin_regions(image, global_mask)

    # Check if there are any pixels in the skin mask
    if not np.any(skin_regions):
        print("No skin pixels detected. Returning empty skin regions.")
        return np.zeros_like(skin_regions, dtype=np.uint8), HSV_mask, YCrCb_mask

    return skin_regions, HSV_mask, YCrCb_mask

def refine_skin_regions(image, skin_mask):
    # Apply additional refinement to the skin regions
    kernel = np.ones((5, 5), np.uint8)
    skin_mask = cv2.morphologyEx(skin_mask, cv2.MORPH_OPEN, kernel)
    skin_mask = cv2.morphologyEx(skin_mask, cv2.MORPH_CLOSE, kernel)

    # Remove small regions
    num_labels, labels, stats, centroids = cv2.connectedComponentsWithStats(skin_mask.astype(np.uint8))
    skin_regions = np.zeros_like(skin_mask, dtype=bool)
    for i in range(1, num_labels):
        if stats[i, 2] >= 100 and stats[i, 3] >= 100:  # Use 2 for width and 3 for height
            skin_regions[labels == i] = True

    return skin_regions

def draw_skin_regions(image, skin_regions):
    # Convert skin_regions to a data type supported by findContours (e.g., uint8)
    skin_regions_uint8 = skin_regions.astype(np.uint8)

    # Green color for filling skin regions
    green = (0, 255, 0)

    # Find contours and fill skin regions
    contours, _ = cv2.findContours(skin_regions_uint8, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    for contour in contours:
        cv2.fillConvexPoly(image, contour, green)

    return image

def classify_skin_color(avg_skin_color):
    # Define RGB ranges for different skin tones
    light_skin = 150
    medium_skin = 80
    dark_skin = 40

    # Classify skin color
    if avg_skin_color >= light_skin:
        return "white"
    elif avg_skin_color >= medium_skin:
        return "asian"
    elif avg_skin_color >= dark_skin:
        return "black"
    else:
        return "알 수 없음"

def extract_skin_color(image, skin_regions):
    # Extract skin pixels
    skin_pixels = image[skin_regions]

    # Calculate average skin color
    avg_skin_color = np.mean(skin_pixels, axis=(0, 1))  # Calculate mean along the color channels

    return avg_skin_color

# Suppress KMeans memory leak warning (optional)
import os
os.environ["OMP_NUM_THREADS"] = "1"

# Load the image
image = cv2.imread("/content/gdrive/MyDrive/AI_IMAGE/27.jpg")

# Process the image and extract skin regions
skin_regions, HSV_mask, YCrCb_mask = extract_skin_regions(image.copy())

# Draw skin regions on the image copy
image_with_skin = draw_skin_regions(image.copy(), skin_regions)

# Extract average skin color
avg_skin_color = extract_skin_color(image, skin_regions)
print("Average skin color (BGR):", avg_skin_color)

# Classify skin color
skin_color_class = classify_skin_color(avg_skin_color)
print("Skin color class:", skin_color_class)

# Display the image with skin regions
plt.imshow(cv2.cvtColor(image_with_skin, cv2.COLOR_BGR2RGB))
plt.title("Skin Detection")
plt.show()

# Visualize HSV and YCrCb masks
plt.figure(figsize=(10, 5))
plt.subplot(1, 2, 1)
plt.imshow(HSV_mask, cmap='gray')
plt.title("HSV Mask")
plt.subplot(1, 2, 2)
plt.imshow(YCrCb_mask, cmap='gray')
plt.title("YCrCb Mask")
plt.show()

# Visualize skin regions
plt.imshow(skin_regions, cmap='gray')
plt.title("Skin Regions")
plt.show()