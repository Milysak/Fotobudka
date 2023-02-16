from flask import Flask, request, send_file, make_response
from reportlab.pdfgen import canvas
from PIL import Image, ImageEnhance
import io
import os
import numpy as np
from datetime import date, datetime
from fpdf import FPDF
from PIL import Image, ImageOps

app = Flask(__name__)

banners = ["colorfulbanner.jpg", "darknessbanner.jpg", "silvesterbanner.jpg", "christmasbanner.jpg",]

folderForPhotos = list()
listOfPhotosNames = list()

pathToFolder = '.\\Photos\\'


def GetCurrentTime():
    now = datetime.now()
    return now.strftime('PDF_%d-%m_%H-%M-%S')


@app.route('/postIMG', methods=['POST'])
def postIMAGE():
    filterID = int(request.form.get('filterID'))
    file = request.files.get('image')

    image = Image.open(file)

    if filterID == 1:
        image = ImageOps.grayscale(image)
    elif filterID == 2:
        image = ImageEnhance.Color(image)
        image = image.enhance(1.5)

    image.save(f'{pathToFolder}{file.filename}')

    # print(len(folderForPhotos))
    # print(folderForPhotos[len(folderForPhotos) - 1])

    return "Done!"


@app.route('/createPDF', methods=['POST', 'GET'])
def createPDF():
    bannerID = int(request.form.get('bannerID'))

    pdfH = 297
    pdfW = 210

    banner = banners[bannerID]

    pdf = FPDF()
    pdf.add_page()

    bannerImage = Image.open(f'.\\Banners\\{banner}')

    pdf.image(bannerImage, 0, 0, w=300, h=300)

    coodsX = 0
    coodsY = 0

    # Get a list of all files in the folder
    files = os.listdir(pathToFolder)

    # Filter out files that are not images
    images = [file for file in files if file.endswith(".jpg") or file.endswith(".png")]

    counter = 0
    # Open and display each image
    while counter < 12:
        for image in images:
            if counter == 6:
                coodsX = 105
                coodsY = 0
            img = Image.open(os.path.join(pathToFolder, image))

            pdf.image(img, coodsX, coodsY, w=70, h=50)
            coodsY += 50
            counter += 1

    PDFpath = f'.\\resultsPDF\\{GetCurrentTime()}{bannerID}.pdf'
    print(PDFpath)
    pdf.output(PDFpath)

    # Delete photos from buffer folder
    for file in files:
        filePath = f'{pathToFolder}{file}'

        if os.path.exists(filePath):
            os.remove(filePath)

    return "Done!"


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)
