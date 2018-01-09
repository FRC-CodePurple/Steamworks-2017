#-------------------------------------------------------------------------------
# Name:        module1
# Purpose:
#
# Author:      a whole bunch of people
#
# Created:     21/01/2017
# Copyright:   (c) s-EKOHAGEN 2017
# Licence:     i'm not old enough to drive ;_;
#              Then use my license lmao -Kaity
#-------------------------------------------------------------------------------
from grip import GripPipeline
import asciii
import argparse
import cv2
import math
import numpy
from client import Client
import time

FOV_VERTICAL = 34.3
FOV_HORIZONTAL = 61.0
TOTALPIXELHEIGHT = 720

client = Client()

#camera loop; displays raw video feed indefinitely until you press q
#while(True):
    #success, image = cam.read()
    #success is a boolean (1 if taking pic was successful)

    #displays unprocessed pic
    #3cv2.imshow(' ',image)

    #test filter:
    #gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    #don't mess with the hex stuff, if it ain't broke don't fix it
    #if cv2.waitKey(1) & 0xFF == ord('q'):
        #break

#releases camera:
#cam.release()
#elizabeth's fave function:
#cv2.destroyAllWindows()

def findBottomLeft(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    result = (avgX, avgY)
    for p in points: #Find bottom left corner (Y coord > avgY, X coord < avgX)
        if(result[1] < p[0,1] and result[0] > p[0,0]):
            result = (p[0,0], p[0,1])
    return result

def findBottomRight(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    result=(avgX, avgY)
    for p in points: #Find bottom right corner (Y coord > avgY, X coord > avgX)
        if(result[1] < p[0,1] and result[0] < p[0,0]):
            result = (p[0,0], p[0,1])
    return result

def findUpperLeft(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    result = (avgX, avgY)
    for p in points: #Find upper left corner (Y coord < avgY, X coord < avgX)
        if(avgY > p[0,1] and avgX > p[0,0]):
            result = (p[0,0], p[0,1])
    return result
    
def findUpperRight(points):
    avgX = numpy.average(points[:,0,0])#Find average X value of corners
    avgY = numpy.average(points[:,0,1])#Find average Y value of corners
    result = (avgX, avgY)
    for p in points: #Find upper right corner (Y coord < avgY, X coord > avgX)
        if(avgY > p[0,1] and avgX < p[0,0]):
            result = (p[0,0], p[0,1])
    return result
    
def getCorners(contour):
    if (len(contour) < 4):
        return None, None, None, None, None
    e = 0.05*cv2.arcLength(contour, True)
    approx = cv2.approxPolyDP(contour,e,True)
    #print('len(approx)', len(approx))
    bottomLeft = findBottomLeft(approx)
    upperLeft = findUpperLeft(approx)
    bottomRight = findBottomRight(approx)
    upperRight = findUpperRight(approx)
    return approx, upperLeft, bottomLeft, upperRight, bottomRight
    
def findRatios(contours):
    i = 0
    result = []
    while (len(contours) > i ):
        #print('contours[i]', contours[i])
        approx, upperLeft, bottomLeft, upperRight, bottomRight = getCorners(contours[i])
        heightL = bottomLeft[1]-upperLeft[1]
        heightR = bottomRight[1]-upperRight[1]
        avgHeight = (heightL+heightR)/2
        lengthB = bottomRight[0]-bottomLeft[0]
        lengthT = upperRight[0]-upperLeft[0]
        avgLength = (lengthB+lengthT)/2
        #print(avgHeight/avgLength)
        if((avgHeight/avgLength) < 2):
            #contours = numpy.delete(contours, i)
            #print('len(contours)', len(contours))
            #i=0
            print('ratio', avgHeight/avgLength)
        else:
            result.append(contours[i])
            if (i==len(contours)):
                break
        i = i+1
    contours = numpy.array(result)
    return contours

def findCoordinates(contours):
    d = len(contours)-1
    i=0
    while (len(contours)>2):
        #print("len")
        #print(d)
        e = 0.05*cv2.arcLength(contours[i], True)
        approx = cv2.approxPolyDP(contours[i],e,True)
        e2 = 0.05*cv2.arcLength(contours[d], True)
        approx2 = cv2.approxPolyDP(contours[d],e,True)
        upperLeft = findUpperLeft(approx)
        upperLeft2 = findUpperLeft(approx2)
        #print(upperLeft)
        #print(upperLeft2)
        #print("diff")
        #print(abs(upperLeft[0] - upperLeft2[0]))
        #print(abs(upperLeft[1] - upperLeft2[1]))
        if(i == d):
            i = i+1
            d = len(contours)-1            
        elif(abs(upperLeft[0] - upperLeft2[0]) <= 2 and abs(upperLeft[1] - upperLeft2[1]) <= 2):#compare with tolerance of 2            
            print(len(contours))
            contours.remove(contours[i])
            d = d-1
        else:
            d = d-1
    return contours

def filterContours(image, contours):    
    leftTarget = None
    rightTarget = None
    
    contours = findRatios(contours)
    #contours = findCoordinates(contours)
    #cv2.drawContours(image, contours, -1,(255,0,0),2)
    if (len(contours)<2):
        return leftTarget, rightTarget, None, None
    approx, upperLeft, bottomLeft, upperRightL, bottomRightL = getCorners(contours[0])
    approx2, upperLeftR, bottomLeftR, upperRight, bottomRight = getCorners(contours[1])
    leftTarget = [upperLeft, bottomLeft, upperRightL, bottomRightL]
    rightTarget = [upperLeftR, bottomLeftR, upperRight, bottomRight]

    if(upperLeft[0] > upperRight[0]):
        leftTarget = [upperLeftR, bottomLeftR, upperRight, bottomRight]
        rightTarget = [upperLeft, bottomLeft, upperRightL, bottomRightL]
    return leftTarget, rightTarget, approx, approx2

def getDistanceTarget(phLeftTarget, phRightTarget):
    ht1 = 9.625               
    h = 5.0
    Pt1 = phLeftTarget * (ht1/h)
    b1 = numpy.deg2rad(FOV_VERTICAL * (Pt1/TOTALPIXELHEIGHT))
    distanceLeftTarget = ht1/numpy.tan(b1)
    #print (distanceLeftTarget)

    ht1 = 9.625
    h = 5.0
    Pt1 = phRightTarget * (ht1/h)
    b1 = numpy.deg2rad(FOV_VERTICAL * (Pt1/TOTALPIXELHEIGHT))
    distanceRightTarget = ht1/numpy.tan(b1)

    return distanceLeftTarget, distanceRightTarget

def getPosition(distanceLeftTarget, distanceRightTarget, points):
    #Look vertically from top.
    T = 10.25
    angleB = numpy.arccos((T*T + distanceRightTarget*distanceRightTarget - distanceLeftTarget*distanceLeftTarget)/(2*T*distanceRightTarget))
    angleA = numpy.arccos((T*T + distanceLeftTarget*distanceLeftTarget - distanceRightTarget*distanceRightTarget)/(2*T*distanceLeftTarget))
    angleD = numpy.pi - angleA
    Y = (numpy.sin(angleB))*distanceRightTarget
    X = (numpy.cos(angleD))*distanceLeftTarget
    Z = X + (T/2)
    avgX = numpy.average([p[0] for p in points])#Find average X value of corners
    print('avgX', avgX)
    horizontalAngle = FOV_HORIZONTAL * (avgX - 1280/2) / 1280
    print('horizontalAngle', horizontalAngle)
    #When Y is a certain distance, it may be necessary to move back in order to have the ability to place gear on target
    #Z is the length that is moved in order to get in front of the target
    displacement = numpy.sqrt((Z**2 + Y**2))
    print('displacement', displacement)
    print('Z', Z)
    print('Y', Y)
    print('X', X)
    print('distanceLeftTarget', distanceLeftTarget)
    print('distanceRightTarget', distanceRightTarget)
    print('angleA', angleA/numpy.pi * 180.0)
    print('angleB', angleB/numpy.pi * 180.0)
    values = [displacement, Z, Y, horizontalAngle]
    client.put('values', values)#- left, +right
    return 

#pseudocode is commented out, actual code isn't
def main():
    #init camera
    cam = cv2.VideoCapture(0)
    cam.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
    cam.set(cv2.CAP_PROP_FRAME_HEIGHT, TOTALPIXELHEIGHT)
    while (not cam.isOpened):
        cam = cv2.VideoCapture(0)
        time.sleep(1)
    while True:
        success, image = cam.read()
        if success:
            break
        time.sleep(1)
    while True:
        #if button is pressed: v = 1
        while (True):
            success,image=cam.read()
            #cv2.imshow('contours', image)
            #print("success?", success)
            #print("image shape", image.shape)
            #if cv2.waitKey(1) & 0xFF == ord('q'):
            #    break
            #cv2.imshow('raw img', image)
            gp = GripPipeline()
            gp.process(image)
            thresh = gp.hsl_threshold_output
            cv2.imshow('thresh', thresh)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
            contours = gp.filter_contours_output
            print("contour length", len(contours))
            print('image.shape', image.shape)

            #cv2.drawContours(image, contours, -1,(255,0,255),2)
            
            #draws contours
            '''e = 0.05*cv2.arcLength(contours[0], True)
            approx = cv2.approxPolyDP(contours[0],e,True)
            e2 = 0.05*cv2.arcLength(contours[1], True)
            approx2 = cv2.approxPolyDP(contours[1],e,True)
            #for c in contours:  
            cv2.drawContours(image, approx,-1,(0,255,0),2)
            cv2.drawContours(image, approx2,-1,(0,255,0),2)
            cv2.imshow('contours', image)'''

            #contour filtering
            if(len(contours) >= 2):
                leftTarget, rightTarget, approx, approx2 = filterContours(image, contours)

                cv2.drawContours(image, approx,-1,(0,255,0),2)
                cv2.drawContours(image, approx2,-1,(0,255,0),2)
                cv2.imshow('contours', image)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
                #print(leftTarget) 
                #print(rightTarget)
                if leftTarget != None:
                    points = [leftTarget[0], leftTarget[1], rightTarget[2], rightTarget[3]]
                    distanceLeftTarget, distanceRightTarget = getDistanceTarget(leftTarget[1][1] - leftTarget[0][1], rightTarget[3][1] - rightTarget[2][1])
                    getPosition(distanceLeftTarget, distanceRightTarget, points)

                
            '''if cv2.waitKey(1) & 0xFF == ord('q'):
                break'''
        

    '''cam.release()
    cv2.destroyAllWindows()'''



if __name__ == '__main__':
    main()







'''pseudocode for our final program
v = 0
while True:
    take a raw pic
    display the pic

while True:
    if button is pressed:
        v = 1
        while v == 1:
        
            cam = cv2.VideoCaptur(0)
            success, image = cam.read()
            gp = GripPipeline
            gp.process(image)
            contours = gp.filter_contours_output
            if (len(contours) >= 2):
                
                run contour filtering stuff
                run the math algorithms on the points found

                if angle and 2 distances (first dist, second dist make the legs of a right triangle) are within a certain range:
                    send the angle and the 2 distances to the roborio
                    the gyro will help the robot turn the correct angle, then drive the correct distance, then turn 90 deg.
                    once the robot is facing the target, the camera can check if its @ the right angle

                    gap method!!!
                    if gap from left corner to left edge == right corner to right edge:
                        run contour filtering stuff
                        run math algorithms on the points found
                        get the new second distance from math algorithm

                        if new angle and 2 new distances aren't within a certain range:
                            tell driver to move robot, start loop over again

                        else:
                            if new angle and 2 distances are within a certain range:
                                drive a little less than necessary for the final placement of gear, driver can make small adjustments as needed
                            elif (new second distance + old second distance from first pic)/2 aren't within a certain range:
                                get permission from driver to keep going

                    else: tell driver to move robot, start loop over again

                elif angle and 2 distances (first dist, second dist make the legs of a right triangle) aren't within a certain range:
                    tell driver to move robot, start loop over again


                
            else:
                tell driver to move robot, start loop over again'''
               
                
