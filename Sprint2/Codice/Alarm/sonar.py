import time
import math

num = 0
gradient = 10 #how much space between the x for generate the data(the lowest the more values), between 1 and 199
file = open('sonar.txt', 'w')

while True:

    outnumber =  50 * (math.cos(0.01 * math.pi * num) + 1)
    #outnumber=500
    print(str(outnumber))
    file.write(str(outnumber)+"\n")

    num = (num + gradient) % 200
    time.sleep(1) #sleep one second

file.close()
