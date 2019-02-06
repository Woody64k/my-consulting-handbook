function mkCert(){
	if [ ${#} = 0 ]; then
		echo "Erzeugt schlüssel und Zertifikate fur die Entwicklung."
		echo "  Param 1: Ordner in welchem die Zertifikate erzeugt werden sollen."
		echo "  Param 2: Name der Zertifikate."
	else
		RUN_FOLDER=$1
		NAME=$2

		cd $RUN_FOLDER
		mkdir certs
		cd certs/
		# Generating RSA private key
		openssl genrsa -des3 -passout pass:x -out ${NAME}.pass.key 2048
		
		# writing RSA key
		openssl rsa -passin pass:x -in ${NAME}.pass.key -out ${NAME}.key
		
		# Generate Certificate 
		openssl req -new -key ${NAME}.key -out ${NAME}.csr
			# Country Name (2 letter code) [AU]: DE
			# State or Province Name (full name) [Some-State]: BAVARIA
			# Locality Name (eg, city) []: Nuernberg
			# Organization Name (eg, company) [Internet Widgits Pty Ltd]:MID GMBH
			# Organizational Unit Name (eg, section) []:Consulting
			# Common Name (e.g. server FQDN or YOUR name) []:tools.mid.de
			# Email Address []:s.sauerbier@mid.de
		 
			# Please enter the following 'extra' attributes
			# to be sent with your certificate request
			# A challenge password []:
			# An optional company name []:
		
		openssl x509 -req -sha256 -days 365 -in ${NAME}.csr -signkey ${NAME}.key -out ${NAME}.crt
			# Signature ok
			# subject=/C=DE/ST=BAVARIA/L=Nuernberg/O=MID GMBH/OU=Consulting/CN=tools.mid.de/emailAddress=s.sauerbier@mid.de
			# Getting Private key
	fi
}