# -*- coding: utf-8 -*-
"""
Created on Thu Jan 26 13:48:00 2017

@author: s-KCOLBERT
"""

import math, numpy

#CV style (y,x) pixels
lowerRight = numpy.array([700, 768]) #Right target, lower right
lowerLeft = numpy.array([678, 264]) #Left target, lower left

upperRight = numpy.array([452, 730]) #Right target, upper right
upperLeft = numpy.array([308, 272]) #Left target, upper left

actualHeight = 5 #inches
actualWidth = 10.25 #inches

#==========Distance from Camera to Target================
 
A = (33.0(a)/1280)
B = (180-(A+C))
C = (180-(A+(math.sin**-1((b-math.sin(A))/(a))
a = ???? #half the length of the target/ size of target on screen (opposite side to FOV angle)
b = ((h1)/math.tan(b1)) #distance from camera to target 
b = ((h2)/math.tan(b2)) #same as above, both get more accurate calculations
b1 = (math.pi*(math.tan**-1(h1*b)))
b2 = (math.pi*(math.tan**-1(h2*b)))
h1 = ((math.tan(b1))*(b))
h2 = ((math.tan(b2))*(b))
h = h1+h2 #should be height of the target in pixels on screen
F = b1+b2 = (math.pi*33.1) #should be 33.1. field of view (angle in radians)(vertically...change to horizontal once found
x = ((2*(math.pi))*(b))*(90-((A)+(math.sin**-1((b)-math.sin*sin(A))/(a)) #

#========Position of Camera in Relation to Target==========

leftHeight = lowerLeft[0] - upperLeft[0]
rightHeight = lowerRight[0] -upperRight[0]

topWidth = upperRight[1] - upperLeft[1]
bottomWidth = lowerRight[1] - lowerLeft[1]

#Pixels to Inches Conversion (pixels/dpi)
print leftHeight/96.0
print rightHeight/96.0
print topWidth/96.0
print bottomWidth/96.0

if leftHeight > rightHeight:
    print "Left of target"
elif rightHeight > leftHeight:
    print "Right of target"
else:
    print "on target"
    
#Find Center?
print (upperRight[1] + upperLeft[1])/2
print (upperLeft[0] + lowerLeft[0])/2
 
