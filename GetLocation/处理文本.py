import sys
import os
import re

def changefile(filename):   
    with open(filename,'r') as FILEIN:
        with open("New"+filename,'w') as FILEOUT:
            for line in FILEIN.readlines():
                if not line.strip():continue    #判断一下，如果行为空则继续
                arr = line.split()
                newline = r'provinceMap.put("' + arr[0]+ r'", "' + arr[1] + r'");'
                print >>FILEOUT, newline


    FILEIN.close()
    FILEOUT.close()


if __name__ == '__main__':
    #changefile(sys.argv[1])
    changefile("City.txt")
    
            
        
    
    
