# TransformX-Service-UCD-ClosingDisclosurePdf

This project defines the code for generating the PDF for Closing Disclosure

This service runs on port: 9013

To run the server ,enter into project folder and run

mvn spring-boot:run (or) java -jar *location of the jar file*

The above line will start the server at port 9013

If you want to change the port .Please start th server as mentioned below 

syntax : java -jar *location of the jar file* --server.port=*server port number*
 
example: java -jar target/LoanEstimatePdf.jar --server.port=9090

API to generate Loan Estimate PDF(/actualize/transformx/documents/ucd/cd/{version}/pdf) with input as Closing Disclosure XML 

syntax : *server address with port*/actualize/transformx/documents/ucd/cd/v1/pdf; method :POST; Header: Content-Type:application/xml

API to check status of service(/actualize/transformx/documents/ucd/cd/{version}/ping)

syntax : *server address with port*/actualize/transformx/documents/ucd/cd/v1/ping; method: GET;

example: http://localhost:9013/actualize/transformx/documents/ucd/cd/pdf ; method: POST; Header: Content-Type:application/xml