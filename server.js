// Imports

var express = require('express')
var session = require('express-session')
var { JSDOM } = require('jsdom')
var app = express()
var path = require('path')
var bodyParser = require('body-parser')
var fs = require('fs')
var Web3 = require('web3')
var contract = require('truffle-contract')
var adminJSON = require("./build/contracts/OLVoteSys.json")
var voterJSON = require("./build/contracts/Voter.json")

// Global Variables

var file
var web3
var adminContract
var voterContract
var adminInstance
var voterInstance
var accounts
var errorFile

var networkID
var userRegistered
var voterDash
var adminDash
var voteFile

var voterID
var password

var halt = false

// App attributes 

app.use(bodyParser.urlencoded({ extended: false }))
app.use(session({
    resave: true,
    saveUninitialized: true,
    secret: "secret",
}))
app.listen(10000)

// Loading Web3 and accounts

async function loadWeb3() {
    web3 = new Web3(new Web3.providers.HttpProvider("http://localhost:7545"))
    accounts = await web3.eth.getAccounts()
    // console.log("Accounts", accounts)
}
loadWeb3()

// Deploy OLVoteSys if not deployed, otherwise load it

async function init() {
    networkID = await web3.eth.net.getId()
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    try {
        adminInstance = await adminContract.deployed()
        console.log("Got instance at ", adminInstance.address)
    } catch (err) {
        console.log("No admin Contract found, deploying new Admin Contract", err)
        try {
            accounts = await web3.eth.getAccounts()
            console.log("Accounts", accounts[0])
            adminInstance = await adminContract.new({ from: accounts[0] })
            console.log("Contract deployed at", adminInstance.address)
        } catch (err) {
            console.log("Error", err)
        }

    }
}
init()

// Functions

async function registerVoter(name, ID, age, password) {
    networkID = web3.eth.net.getId()
    voterContract = contract(voterJSON)
    voterContract.setProvider(web3.currentProvider)
    voterContract.setNetwork(networkID)
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()

    accounts = await web3.eth.getAccounts()

    voterInstance = await voterContract.new(adminInstance.address, name, ID, password, age, { from: accounts[0] })
    console.log("Voter deployed at : ", voterInstance.address)

    await adminInstance.registerVoter(voterInstance.address, { from: accounts[1] })
    var voterNo = await adminInstance.getNoOfVoters()
    voterNo = voterNo.toString()
    console.log(voterNo)
    return voterNo
}

async function getDetails(num) {
    networkID = web3.eth.net.getId()
    voterContract = contract(voterJSON)
    voterContract.setProvider(web3.currentProvider)
    voterContract.setNetwork(networkID)
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    const addressOfVoter = await adminInstance.getVoterAddress(num)
    voterInstance = await voterContract.at(addressOfVoter)
    console.log(voterInstance.address)
    var result = await voterInstance.getVoterDetails()
    return result
}

async function getCandidates() {
    var htmlVariable = "<tr><td><h3>Name</h3></td><td><h3>Represents</h3></td><td><h3>Age</h3></td><td></td></tr>"
    networkID = web3.eth.net.getId()
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    var noOfCandidates = await adminInstance.getNoOfCandidates()
    for (var i = 0; i < noOfCandidates; i++) {
        var result = await adminInstance.showCandidate(i)
        htmlVariable += "<tr>"
        htmlVariable += "<td><h5>" + result[0] + "</h5></td>"
        htmlVariable += "<td><h5>" + result[1] + "</h5></td>"
        htmlVariable += "<td><h5>" + result[2] + "</h5></td>"
        htmlVariable += "<td>" + "<button name='votebutton' onclick='sure("
            + i + ")' value='' class='w3-button w3-light-gray'>Vote</button>" + "</td>"
        htmlVariable += "</tr>"
    }
    // console.log(htmlVariable)
    return htmlVariable
}

async function getCandidatesForAdmin() {
    var htmlVariable = "<tr><td>Name</td><td>Represents</td><td>Age</td><td>No Of Votes Received</td></tr>"
    networkID = web3.eth.net.getId()
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    var noOfCandidates = await adminInstance.getNoOfCandidates()
    for (var i = 0; i < noOfCandidates; i++) {
        var result = await adminInstance.showCandidate(i)
        htmlVariable += "<tr>"
        htmlVariable += "<td>" + result[0] + "</td>"
        htmlVariable += "<td>" + result[1] + "</td>"
        htmlVariable += "<td>" + result[2] + "</td>"
        htmlVariable += "<td>" + result[3] + "</td>"
        htmlVariable += "</tr>"
    }
    // console.log(htmlVariable)
    return htmlVariable
}

async function checkIfEligible(num) {
    networkID = web3.eth.net.getId()
    voterContract = contract(voterJSON)
    voterContract.setProvider(web3.currentProvider)
    voterContract.setNetwork(networkID)
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    const addressOfVoter = await adminInstance.getVoterAddress(num)
    voterInstance = await voterContract.at(addressOfVoter)
    console.log(voterInstance.address)

    var result = await voterInstance.isEligible()
    return result
}

async function registerCandidate(name, repr, age) {
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    accounts = await web3.eth.getAccounts()
    await adminInstance.registerCandidate(name, repr, age, { from: accounts[2] })
    return true
}

async function doVote(to, num) {
    networkID = web3.eth.net.getId()
    voterContract = contract(voterJSON)
    voterContract.setProvider(web3.currentProvider)
    voterContract.setNetwork(networkID)
    adminContract = contract(adminJSON)
    adminContract.setProvider(web3.currentProvider)
    adminContract.setNetwork(networkID)
    adminInstance = await adminContract.deployed()
    const addressOfVoter = await adminInstance.getVoterAddress(num)
    voterInstance = await voterContract.at(addressOfVoter)
    accounts = await web3.eth.getAccounts()
    await voterInstance.vote(to, { from: accounts[3] })
    await adminInstance.vote(to, { from: accounts[3] })
    return true
}

// Request and Response

app.get('/', function (request, response) {
    try {
        voterID = undefined
        response.sendFile(path.join(__dirname + "/index.html"))
    } catch(err) {
        file = fs.readFileSync(__dirname + "/error.html")
        errorFile = new JSDOM(file)
        errorFile.window.document.getElementById("errorMessage").innerHTML = err
        response.send(errorFile.serialize())
    }
})

app.get('/register', function (request, response) {
    if(!halt) {
        try {
            response.sendFile(path.join(__dirname + "/register.html"))
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    }
    else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/login', function (request, response) {
    if(!halt) {
        try {
            response.sendFile(path.join(__dirname + "/login.html"))
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile)
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/registerCandidate', function (request, response) {
    if(!halt) {
        try {
            response.sendFile(path.join(__dirname + "/adminRegister.html"))
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.post('/registerVoter', async function (request, response) {
    if(!halt) {
        try {
            // console.log(request.body)
            var voterNo = await registerVoter(request.body.voterName, request.body.voterID, request.body.voterAge, request.body.voterPassword)
            console.log("returned", voterNo)
            file = fs.readFileSync(__dirname + "/userRegistered.html").toString()
            userRegistered = new JSDOM(file)
            userRegistered.window.document.getElementById("voterID").innerHTML = "<b>" + (voterNo - 1) + "</b>"
            response.send(userRegistered.serialize())
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/loginVoter', async function (request, response) {
    if(!halt) {
        try {
            if (voterID == undefined) {
                if (request.query.voterID == 'admin') {
                    response.redirect('/adminDash')
                }
                else {
                    file = fs.readFileSync(__dirname + "/voterDash.html").toString()
                    voterDash = new JSDOM(file)
                    var details = await getDetails(request.query.voterID)
                    password = request.query.voterPassword
                    voterID = request.query.voterID
                    if(details[4] == password) {
                        console.log(details)
                        voterDash.window.document.getElementById("loginNum").innerHTML = voterID
                        voterDash.window.document.getElementById("userDet").innerHTML = "Hello, " + details[0]
                        voterDash.window.document.getElementById("loginName").innerHTML = details[0]
                        voterDash.window.document.getElementById("loginIDProof").innerHTML = details[1]
                        voterDash.window.document.getElementById("loginAge").innerHTML = details[2]
                        console.log("My password : " + details[4])
                        if (details[3] == -1) {
                            voterDash.window.document.getElementById("loginVotedTo").innerHTML = "Not Voted"
                        } else {
                            voterDash.window.document.getElementById("loginVotedTo").innerHTML = details[3]
                        }
                        response.set('voterID', request.query.voterID)
                        response.send(voterDash.serialize())
                    } else {
                        response.send("Wrong Password for Voter ID")
                    }
                }
            }
            else {
                try {
                    file = fs.readFileSync(__dirname + "/voterDash.html").toString()
                    voterDash = new JSDOM(file)
                    var details = await getDetails(voterID)
                    console.log(details)
                    if (await checkIfEligible(voterID)) {
                        voterDash.window.alert("You are not eligible for voting now")
                        voterDash.window.document.getElementById('voteFlag').remove
                    }
                    voterDash.window.document.getElementById("loginNum").innerHTML = voterID
                    voterDash.window.document.getElementById("userDet").innerHTML = "Hello, " + details[0]
                    voterDash.window.document.getElementById("loginName").innerHTML = details[0]
                    voterDash.window.document.getElementById("loginIDProof").innerHTML = details[1]
                    voterDash.window.document.getElementById("loginAge").innerHTML = details[2]
                    if (details[3] == -1) {
                        voterDash.window.document.getElementById("loginVotedTo").innerHTML = "Not Voted"
                    } else {
                        voterDash.window.document.getElementById("loginVotedTo").innerHTML = details[3]
                    }
                    console.log(voterDash.window.document.getElementById('voteFlag').hidden)
                    response.send(voterDash.serialize())
                } catch (err) {
                    response.send("Error caught" + err)
                }
            }
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    }
    else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/vote', async function (request, response) {
    if(!halt) {
        try {
            if (await checkIfEligible(voterID)) {
                console.log(voterID)
                var allCandidates = await getCandidates()
                file = fs.readFileSync(__dirname + "/vote.html").toString()
                voteFile = new JSDOM(file)
                voteFile.window.document.getElementById('loginPageVoteTable').innerHTML = allCandidates
                // console.log(voteFile.window.document.getElementById('loginPageVoteTable').innerHTML)
                response.send(voteFile.serialize())
            } else {
                console.log("Voter ID", voterID)
                response.redirect('/loginVoter')
            }
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/adminDash', async function (request, response) {
    if(!halt) {
        try {
            var allCandidates = await getCandidatesForAdmin()
            file = fs.readFileSync(__dirname + "/adminDash.html").toString()
            adminDash = new JSDOM(file)
            adminDash.window.document.getElementById('adminDashTable').innerHTML = allCandidates
            response.send(adminDash.serialize())
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        try {
            var allCandidates = await getCandidatesForAdmin()
            file = fs.readFileSync(__dirname + "/adminDash.html").toString()
            adminDash = new JSDOM(file)
            adminDash.window.document.getElementById('adminDashTable').innerHTML = allCandidates
            adminDash.window.document.getElementById('finish').remove()
            response.send(adminDash.serialize())
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    }
})

app.get('/registerCandidate', async function (request, response) {
    if(!halt) {
        try {
            response, sendFile(path.join(__dirname + "/adminRegister.html"))
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.post('/CandidateRegistered', async function (request, response) {
    if(!halt) {
        try {
            // console.log("Details of new Candidate are " , request.body.nameOfCandidate, request.body.representing, request.body.ageOfCandidate)
            var result = await registerCandidate(request.body.nameOfCandidate, request.body.representing, request.body.ageOfCandidate)
            response.redirect("/adminDash")
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/voterVoted', async function (request, response) {
    if(!halt) {
        try {
            console.log("Number", request.query.voteTo)
            await doVote(request.query.voteTo, voterID)
            response.redirect('/loginVoter')
        } catch(err) {
            file = fs.readFileSync(__dirname + "/error.html")
            errorFile = new JSDOM(file)
            errorFile.window.document.getElementById("errorMessage").innerHTML = err
            console.log(err)
            response.send(errorFile.serialize())
        }
    } else {
        // response.send("Election has ended")
        response.sendFile(path.join(__dirname + "/halted.html"))
    }
})

app.get('/haltElection', async function (request, response) {
    console.log(halt)
    halt = true
    console.log(halt)
    response.redirect(adminDash)
})