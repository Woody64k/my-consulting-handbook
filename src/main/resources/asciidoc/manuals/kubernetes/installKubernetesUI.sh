# Zertifikat einrichten
kubectl create secret generic kubernetes-dashboard-certs --from-file=$HOME/certs -n kube-system
	# secret "kubernetes-dashboard-certs" created

# UI Installieren
kubectl  apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/alternative/kubernetes-dashboard.yaml
	# serviceaccount "kubernetes-dashboard" created
	# role "kubernetes-dashboard-minimal" created
	# rolebinding "kubernetes-dashboard-minimal" created
	# deployment "kubernetes-dashboard" created
	# service "kubernetes-dashboard" created	

# UI Anpassen
kubectl -n kube-system get service kubernetes-dashboard
	# NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)   AGE
	# kubernetes-dashboard   ClusterIP   10.108.52.94    <none>        80/TCP    57s
kubectl -n kube-system edit service kubernetes-dashboard
	# Change value for spec.type from "ClusterIP" to "NodePort" . Then save the file (:wq)

# Port prüfen um UI Aufzurufen
kubectl -n kube-system get service kubernetes-dashboard
	# NAME                   TYPE       CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
	# kubernetes-dashboard   NodePort   10.108.52.94   <none>        80:30830/TCP   2h