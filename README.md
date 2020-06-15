# online-decentralized-voting-system

Pre-requisites : 
Truffle
Ganache GUI running on port 7545

To run the project, use the following commands : 
1. truffle migrate or truffle-migrate --reset -> Hard Reset of Smart Contracts
2. npm install
3. npm start

Server will listen to port no 10000

Project is done with the following :
1. Solidity
2. Truffle
3. Normal front end using w3.css template
4. Express.js for server

This project has a server in it. Although Blockchain is used and theoritically there should be no server, but it is a server in a cluster of servers that may be used in Hyperledger or Quorum. 

This server has 2 main tasks, first is to differentiate between Admin Node and Voter Nodes, and allow specific functionality to admin. Important tasks such as Creating Candidates, keep live track of votes gained by candidates are main jobs of admin. 

Admin also has the most important job to End the election on this server. This does not mean that blockchain will be halted or election will end globally. 

Remember I told that this is just 1 server out of a cluster of servers that will handle requests and update and transact on the same blockchain, so shutting down 1 server will allow others to work independently. Following are the use cases of the system : 

# Voter Registration
Voter will register himself/herself with the following credentials
  1. Name
  2. ID Proof - Aadhar Number may be a good choice
  3. Password
  4. Age (Although age may be less than 21, registration may be possible but voting is not possible)
Admin Will not register and will not require a password for login also

# Login
Voters after registration will be shown their ID that has been alloted to them. Users need not remember their blockchain address as Smart Contract is keeping a mapping of user ID to their corresponding addresses. Voters will need to login using ID and password which is stored on Blockchain itself

Admin needs to login before the users and must create candidates, which include minimal information such as their name, representing party/association name and thier age. After candidates have been created, voters will be able to vote for them accordingly.

# Voting 
Voters can vote for any candidate that they wish to using a button next to their name and logic for them voting once only has also been implemented so no voter can double vote. Admin will be able to see live status of votes gained by candidates

# Ending Election
Admin can end election at any time he/she wishes with results already being displayed on screens with live changes, and all voters will not be able to login again. They will be redirected to a new page that shows thank you for voting

# Testing
All Unit Tests are performed on 2 machines connected via the same blockchain. Everything seems to work fine but there are bug fixes that can be done to make the whole project smoother.

# Future Developments
Future Developments are a big part of the project as this project is ready to be deployed on latest generation blockchains such as Hyperledger and Quorum. But there are some issues that need to addressed especially related to scalalbility.
  
