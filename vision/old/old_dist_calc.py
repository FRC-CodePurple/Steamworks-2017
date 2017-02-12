#-------------------------------------------------------------------------------
# Name:        module1
# Purpose:
#
# Author:      s-EKOHAGEN
#
# Created:     21/01/2017
# Copyright:   (c) s-EKOHAGEN 2017
# Licence:     <your licence>
#-------------------------------------------------------------------------------
from grip import GripPipeline
import argparse
import cv2
from math import*
import numpy

def getRightLength(coordR):
    return 640- coordR[0,0] #gets gap from right target edge to edge of screen

def getLeftLength(coordL):
    return coordL[0,0] #gets gap from left edge of screen to left edge of target

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
        
INCHES_HEIGHT_TARGET = 5.0
FOV = 32
HT1 = 2.5
LENGTH_TARGET_INCHES = 10.25

def getDistanceTarget(pixelHeightTarget, distanceLeftTarget, distanceRightTarget, TOTALPIXELHEIGHT):
    Pt1 = pixelHeightTarget * (HT1/INCHES_HEIGHT_TARGET)
    b1 = numpy.deg2rad(FOV * (Pt1/TOTALPIXELHEIGHT))
    distanceTarget = HT1/numpy.tan(b1)

    #Look vertically from top.
    angleB = numpy.arcsin((numpy.sin(((33 * distanceRightTarget)/1280)) * distanceLeftTarget)/distanceRightTarget)    
    Y = numpy.sin(angleB)*distanceRightTarget
    displacement = numpy.sqrt((distanceLeftTarget**2 - Y**2))
    print(displacement)
              
    
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--img", required = True)
    args = parser.parse_args()
    
    image_name = args.img
    image = cv2.imread(image_name)
    screen_length = image.shape[1] #Length (x) of the image
    screen_height = image.shape[0] #Height (y) of the image
    
    #cv2.imshow("unprocessed image", image)
    image2 = image.copy()
    gp = GripPipeline()
   
    gp.process(image2)
    cv2.imshow("blur", gp.blur_output)
    contours = gp.find_contours_output
    print(len(contours)) #Number of contours found
        
    #Left Target
    e = 0.05*cv2.arcLength(contours[6], True)#6
    approx = cv2.approxPolyDP(contours[6],e,True) #Finds 4 corners of a target
    bottomLeft = findBottomLeft(approx)
    print(bottomLeft)
    print (approx) #coordinates of all corners for checking if bottomLeft is correct

    #Right Target
    e2 = 0.1*cv2.arcLength(contours[4], True)#4
    approx2 = cv2.approxPolyDP(contours[4],e2,True) #Finds 4 corners of other target
    bottomRight = findBottomRight(approx2)
    print(bottomRight)
    print (approx2)
    
    cv2.drawContours(image2, approx,-1,(0,255,0),2)
    cv2.drawContours(image2, approx2,-1,(0,255,0),2)
    
    cv2.imshow("contours", image2)

#Get gap between egde of screen and target    
    rightLength = getRightLength(bottomRight)
    leftLength = getLeftLength(bottomLeft)
    print (leftLength)
    print (rightLength)    
                                 #possible tolerance change to 30
    if abs(rightLength-leftLength) < 2: #if the robot is on center with tolerance of 2 px
        print ("on target, move forward") #will eventually give robot actual command or something
    elif rightLength > leftLength:
        print ("move left")
    elif leftLength > rightLength:
        print ("move right")
#gap measured from 1 ft distance is close to where we want to stop
#average gaps, if gap is larger than (some number) be more precise
#repeatedly call (ex: every 1 ft) so that you can tune as you go

    target_length = bottomRight[0,0] - bottomLeft[0,0]
    ratio = target_length/screen_length #gets ratio of target width to screen size
    print (ratio)    
    
    cv2.waitKey(0)
if __name__ == '__main__':
    main()
