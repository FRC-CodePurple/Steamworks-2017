# -*- coding: utf-8 -*-
"""
Created on Sat Jan 28 09:45:16 2017

@author: s-KCOLBERT
"""

import numpy
from matplotlib import pyplot as plt
import scipy

ratios = numpy.array([0.9964, 0.8691, 0.7139, 0.6930, 0.7178, 0.6725])
angles = numpy.array([90, 100, 120, 130, 140, 150])-90

plt.plot(ratios, angles, "*")

def fitTrig(x, a, b, c, d):
    return a*numpy.sin(b*(x +c)) + d

def fitpow(x, a, b, c):
    return a*x**b + c

def fitsq(x, a, b, c): #the most ok
    #lol kaity -elizabeth
    return (a*(x**2)) + (b*x) + c

trig_guess = numpy.array([0.5, 0.05, 0, 0.75])
popt, pcov = scipy.optimize.curve_fit(fitpow, ratios, angles)

x = numpy.linspace(0.6,1)
y = fitpow(x, popt[0], popt[1], popt[2])
print popt
print x
plt.plot(x,y)
