#-------------------------------------------------------------------------------
# Name:        module1
# Purpose:
#
# Author:      a whole bunch of people
#
# Created:     21/01/2017
# Copyright:   (c) s-EKOHAGEN 2017
# Licence:     <i'm not old enough to drive XD>
#-------------------------------------------------------------------------------
from grip import GripPipeline
import argparse
import cv2
import math
import numpy

FOV_VERTICAL = 33
FOV_HORIZONTAL = 58.67
TOTALPIXELHEIGHT = 720

def findBottomLeft(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    for p in points: #Find bottom left corner (Y coord > avgY, X coord < avgX)
        if(avgY < p[0,1] and avgX > p[0,0]):
            return p

def findBottomRight(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    for p in points: #Find bottom right corner (Y coord > avgY, X coord > avgX)
        if(avgY < p[0,1] and avgX < p[0,0]):
            return p

def findUpperLeft(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    for p in points: #Find upper left corner (Y coord < avgY, X coord < avgX)
        if(avgY > p[0,1] and avgX > p[0,0]):
            return p

def findUpperRight(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    for p in points: #Find upper right corner (Y coord < avgY, X coord > avgX)
        if(avgY > p[0,1] and avgX < p[0,0]):
            return p

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--img", required = True)
    args = parser.parse_args()
    image_name = args.img 

    image2 = image.copy()
    gp = GripPipeline()
    gp.process(image2)
    contours = gp.find_contours_output
    print(len(contours)) #Number of contours found

    #Left Target
    e = 0.05*cv2.arcLength(contours[0], True)#6
    approx = cv2.approxPolyDP(contours[0],e,True) #Finds 4 corners of a target
    bottomLeft = findBottomLeft(approx)
    print(bottomLeft)
    upperLeft = findUpperLeft(approx)
    print(upperLeft)
    print (approx) #coordinates of all corners for checking if bottomLeft is correct

    #Right Target
    e2 = 0.1*cv2.arcLength(contours[1], True)#4
    approx2 = cv2.approxPolyDP(contours[1],e2,True) #Finds 4 corners of other target
    bottomRight = findBottomRight(approx2)
    print(bottomRight)
    upperRight = findUpperRight(approx2)
    print(upperRight)
    print (approx2)

    cv2.drawContours(image2, approx,-1,(0,255,0),2)
    cv2.drawContours(image2, approx2,-1,(0,255,0),2)

    cv2.imshow("contours", image2)

    cv2.waitKey(0)

def getDistanceTarget(pixelHeightTarget):
    ht1 = 2.5
    h = 5.0
    Pt1 = pixelHeightTarget * (ht1/h)
    ht1 = 2.5
    b1 = numpy.deg2rad(FOV_VERTICAL * (Pt1/TOTALPIXELHEIGHT))
    distanceTarget = ht1/numpy.tan(b1)
    print (distanceTarget)
    return distanceTarget

def getPosition(distanceLeftTarget, distanceRightTarget):
    #Look vertically from top.
    T = 10.25
    angleB = numpy.arccos((T*T + distanceRightTarget*distanceRightTarget - distanceLeftTarget*distanceLeftTarget)/(2*T*distanceRightTarget))
    Y = numpy.sin(angleB)*distanceRightTarget
    displacement = numpy.sqrt((distanceLeftTarget**2 - Y**2))
    print(displacement)

if __name__ == '__main__':
    main()
