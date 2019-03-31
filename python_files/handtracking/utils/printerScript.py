import socket

HOST = ''
PORT = 5000
MESSAGE = cords
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))
s.send(MESSAGE.encode())




#print(cords)
