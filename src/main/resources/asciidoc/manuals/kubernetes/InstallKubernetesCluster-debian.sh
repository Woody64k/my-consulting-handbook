scp ~/.ssh/id_rsa.pub k8s@192.168.137.45:/tmp/id_rsa.ansible.pub

mkdir ~/.ssh
touch ~/.ssh/authorized_keys
cat /tmp/id_rsa.ansible.pub >> ~/.ssh/authorized_keys

# Add k8s-user to sudo
su root
vi /etc/apt/sources.list
# Remove cdrom lines from list.


apt-get install sudo -y && usermod -aG sudo k8s
exit

# IP für remotezugriff ermitteln
hostname -I




# Netzwerkadresse statisch machen
ip addr show
cp /etc/network/interfaces /etc/network/interfaces.bak
vi /etc/network/interfaces
#-----------------------------
iface eth0 inet dhcp
#-----------------------------
#-> ersetzen mit
#-----------------------------
iface eth0 inet static
        address 192.168.137.20
        netmask 255.255.255.0
        gateway 192.0.1.1
#-----------------------------
# Anschließend
service networking restart     
ifup eth0




# Auf allen Nodes
# Install Docker on all Nodes
sudo apt-get update
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"
   
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
sudo usermod -aG docker k8s

# Docker als systemdienst einrichten
sudo systemctl enable docker 




# Install Kubernetes on all Nodes
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add
sudo apt-add-repository "deb http://apt.kubernetes.io/ kubernetes-$(lsb_release -cs) main"
sudo apt-get update
sudo apt install kubectl

# Swap abschalten, sonst funktioniert Kubernetes nicht korrekt
sudo swapoff -a

# Eindeutige Hostnamen sicherstellen
sudo hostnamectl set-hostname k8s-master
sudo hostnamectl set-hostname k8s-worker-01
sudo hostnamectl set-hostname k8s-worker-02
# Die Uhren müssen ebenfalls syncronisiert werden

# on kubernetes-master
sudo kubeadm init --pod-network-cidr=10.244.0.0/16
# Wie bei Docker Compose gibt der Admin beim start den Join-Befehl mit Parametern preis

# De folgenden Befehle für die Einrichtung des Master werden ebenfalls vorgeschlagen
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config


# Installiere ein Cluster
kubectl apply -f myCubeClusterDescription.yml

# Prüfe die Pods.
kubectl get pods --all-namespaces


# on kubernetes-slave
sudo kubeadm join 10.1.1.9:6443 --token qdjnpd.5glu39uxr92xarsj --discovery-token-ca-cert-hash sha256:ed0684156c718caf425ceae6c85a56c05f7b49037cde3a2f1fd57430a4f58f89

# to check nodes from kubernetes-master
kubectl get nodes

# deploy image from kubernetes-master
kubectl run --image=nginx nginx-server --port=80 --env="DOMAIN=cluster"
kubectl expose deployment nginx-server --port=80 --name=nginx-http

# check running on kubernetes-slave
sudo docker ps

# Ermittelt die IP des Knotens und richtet ein curl an diese IP
kubectl get svc
curl -I 10.101.230.239
