# -*- coding: utf-8 -*-
"""
Created on Sat Jan 28 11:53:22 2017

@author: s-KCOLBERT
"""


def getRightLength(coordR):
    return 1280- coordR[1] #gets gap from right target edge to edge of screen

def getLeftLength(coordL):
    return coordL[1] #gets gap from left edge of screen to left edge of target
    
rightLength = getRightLength(coordR)
leftLength = getLeftLength(coordL)

if abs(rightLength-leftLength) < 2: #if the robot is on center with tolerance of 2 px
    print "on target, move forward" #will eventually give robot actual command or something
elif rightLength > leftLength:
    print "move right"
elif leftLength > rightLength:
    print "move left"
    
#gap measured from 1 ft distance is close to where we want to stop
#average gaps, if gap is larger than (some number) be more precise
#repeatedly call (ex: every 1 ft) so that you can tune as you go