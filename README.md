To run the project run the maven build with the following commands:

start server:
`java -cp target/server-1.0-SNAPSHOT.jar com.gradeprediction.server.ServerStarter`
start client:
`java -cp target/client-1.0-SNAPSHOT.jar com.gradeprediction.client.ClientStarter`

the additional starter classes are necessary for the maven build.
you can run as many clients as you can, the server will handle up to the maximum number of clients allowed which is defined inside the DataServer class.
