import socket
host = "127.0.0.1"
port = 5000
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
datas = cords

try:
    sock.connect((host, port))
except Exception:
    print("java service isn't listening")
    
def send_msg(data):
    sock.send(data.encode())
    
try:
    send_msg(datas)
except Exception:
    sock.close()
        




