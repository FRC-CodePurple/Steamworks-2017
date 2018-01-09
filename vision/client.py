import sys
import time
from networktables import NetworkTables
import logging

class Client:

    def __init__(self):
        # ip address for robot: roboRIO-5827-FRC.local
        NetworkTables.initialize(server='roboRIO-5827-FRC.local')
        self.table = NetworkTables.getTable("table")
        # To see messages from networktables, you must setup logging    
        logging.basicConfig(level=logging.DEBUG)


    def put(self, label, array):
        self.table.putNumberArray(label, array)
   
    def getNumber(self, label):
        try:        
            return self.table.getNumberArray(label)
        except KeyError:
            print('N/A')
            return None

        

    

    
