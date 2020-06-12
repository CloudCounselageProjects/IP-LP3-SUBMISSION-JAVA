var accounts
var file
var indexfile
var resultfile
var adminfile
var candirow
var optrow
var selectedCandi
var candidateCounts
var voterCounts
var CInfo
var VInfo
var ElectionInstance
var ElectionContract
var accounts
var web3


// imports
const Web3 = require('web3');
const fs = require('fs');
const path = require('path');
const bodyparser = require('body-parser');
var  { JSDOM } = require('jsdom');
const  session = require('express-session');
const contract = require('@truffle/contract');
const abiJSON = require("./build/contracts/Election.json");

const express = require('express');
const app = express();


//about app
app.use(bodyparser.urlencoded({ extended: false }));
app.use(bodyparser.json());
app.use(session({
    resave: true,
    saveUninitialized: true,
    secret: "secret",
}))

app.listen(3000, function(){
    console.log('Server has started  on port localhost:3000......');
})
console.log('hello sir');


// loading web3 accounts
async function loadWeb3() {
    web3 = new Web3(new Web3.providers.HttpProvider("http://localhost:7545"))
    accounts = await web3.eth.getAccounts()
    web3.eth.defaultAccount = accounts[0]
    
}
loadWeb3()

async function init() {

    networkID = await web3.eth.net.getId()
    ElectionContract = contract(abiJSON)
    ElectionContract.setProvider(web3.currentProvider)
    ElectionContract.setNetwork(networkID)
    try {
        ElectionInstance = await ElectionContract.deployed()
        var own = await ElectionInstance.owner();
        console.log("Got instance at ", ElectionInstance.address)
        console.log('owner is ', own);
        console.log('\n')

    } catch (err) {
        console.log("No admin Contract found, deploying new Admin Contract", err)
        try {
            accounts = await web3.eth.getAccounts()
            console.log("Accounts", accounts)
            web3.eth.defaultAccount = accounts[0]
            ElectionInstance = await ElectionContract.new({ from: accounts[0] })
            console.log("Contract deployed at", ElectionInstance.address)
        } catch (err) {
            console.log("Error", err)
        }
    }
}
init()


async function candidateCount(res){
    
        var count = await ElectionInstance.candidateCount();
        console.log('got count-->' ,count);
        return count
    
}

async function authvoter(addr ,name,res){
    networkID = await web3.eth.net.getId()
    ElectionContract = contract(abiJSON)
    ElectionContract.setProvider(web3.currentProvider)
    ElectionContract.setNetwork(networkID)
    ElectionInstance = await ElectionContract.deployed();
    accounts = await web3.eth.getAccounts();
    web3.eth.defaultAccount = accounts[0];
    // cheaking that no voter get register again
    var VC = await voterCount();
    
    for (i=1;  i<=VC ; i++){
        VInfo = await getVoterInfo(i)
        if (addr == VInfo['addr']){
            return false;
        }
    }
        var retun = await ElectionInstance.authoriseVoter( addr ,name, {from:accounts[0]});
        console.log( 'authorise receipt ? ', retun);
        return true;    
}

async function dovote(candiID  , addr , res){
    networkID = await web3.eth.net.getId()
    ElectionContract = contract(abiJSON)
    ElectionContract.setProvider(web3.currentProvider)
    ElectionContract.setNetwork(networkID)
    ElectionInstance = await ElectionContract.deployed();
    accounts = await web3.eth.getAccounts();
    web3.eth.defaultAccount = addr;
   
    var retun =await ElectionInstance.vote_candidate(candiID ,addr ,{from:addr} );
    console.log( 'voting recept ? ', retun);
    return true;    
}

async function getCandiInfo(cid ,res) {
    networkID = await web3.eth.net.getId()
    ElectionContract = contract(abiJSON)
    ElectionContract.setProvider(web3.currentProvider)
    ElectionContract.setNetwork(networkID)
    ElectionInstance = await ElectionContract.deployed();
    accounts = await web3.eth.getAccounts();
    web3.eth.defaultAccount = accounts[0];
  
    c = await ElectionInstance.candidates(cid);
    console.log('candi info  -->', c);
    return c ;
}

async function voterCount(res){
    
    var num = await ElectionInstance.voterCount();
    return num 
}

async function getVoterInfo(vid, res){
    networkID = await web3.eth.net.getId()
    ElectionContract = contract(abiJSON)
    ElectionContract.setProvider(web3.currentProvider)
    ElectionContract.setNetwork(networkID)
    ElectionInstance = await ElectionContract.deployed();
    
    var id = await ElectionInstance.VAdress(vid);
    var vinfo = await ElectionInstance.voters(id)
    console.log('Voter Info -->',vinfo)
    return vinfo
}



app.get('/', async function(req, res ){

    res.sendFile(path.join(__dirname + "/welcome.html"))
})


app.get('/login' ,async function(req ,res){
    res.sendFile(path.join(__dirname + "/Alogin.html"))
})

app.get('/admin/authorise' ,async function(req,res){
    try{
        var name = req.query.name
        const addr = req.query.addr
        var username = req.query.UN
        var pass = req.query.pass
      
        if ((name == undefined | addr== undefined) && (username == "addmin" && pass == "adme")){
            console.log("name == empty")
            // res.sendFile(path.join(__dirname + "/admin.html"))
            file = fs.readFileSync(__dirname + '/admin.html', 'utf-8')
            adminfile = new JSDOM(file);
            
            candidateCounts = await candidateCount();
            candirow = adminfile.window.document.querySelector('#Ctable #putcandidates');
            for(i=1; i<=candidateCounts ;i++){
                CInfo = await getCandiInfo(i);
                candirow.innerHTML += "<tr><td> " + CInfo['name'] + "</td> <td > " + CInfo['party'] +"</td></tr>";
            }
            
            voterrow = adminfile.window.document.querySelector('#Vtable #putvoters')
            voterCounts = await voterCount()
            for(i=1; i<=voterCounts ;i++){
                VInfo = await getVoterInfo(i) 
                voterrow.innerHTML += "<tr><td> " + VInfo['name'] + "</td> <td > " +VInfo['addr']+"</td></tr>";
            }
            res.send(adminfile.serialize())

        }else{
            
            
            var result1  = await authvoter(addr , name );
            console.log('authorise ? -->',result1);

            file = fs.readFileSync(__dirname + '/admin.html', 'utf-8')
            adminfile = new JSDOM(file);
            
            candidateCounts = await candidateCount();
            candirow = adminfile.window.document.querySelector('#Ctable #putcandidates');
            for(i=1; i<=candidateCounts ;i++){
                CInfo = await getCandiInfo(i);
                candirow.innerHTML += "<tr><td> " + CInfo['name'] + "</td> <td > " + CInfo['party'] +"</td></tr>";
            }
            
            voterrow = adminfile.window.document.querySelector('#Vtable #putvoters')
            voterCounts = await voterCount()
            for(i=1; i<=voterCounts ;i++){
                VInfo = await getVoterInfo(i) 
                voterrow.innerHTML += "<tr><td> " + VInfo['name'] + "</td> <td > " +VInfo['addr']+"</td></tr>";
            }
            res.send(adminfile.serialize())
            console.log( "this is else loop")
        }

    }catch(err){
        console.log(err);
        file = fs.readFileSync(__dirname + "/error.html")
        errorfile = new JSDOM(file)
        errorfile.window.document.getElementById("errorMessage").innerHTML = err
        res.send(errorfile.serialize());
    }   
})

app.get('/vote' , async function(req , res){
    try{
        file =fs.readFileSync(__dirname + '/vote.html' , 'utf-8');       
        indexfile = new JSDOM(file);
        candidateCounts = await candidateCount();
        candirow = indexfile.window.document.querySelector('#Ctable #putcandidates');
        for(i=1; i<=candidateCounts ;i++){
            CInfo = await getCandiInfo(i);
            candirow.innerHTML += "<tr><td style=padding-left:90px;> " + CInfo['name'] + "</td> <td style=padding-left:90px;>" +CInfo['party']+"</td></tr>";
        }
        optrow =indexfile.window.document.querySelector('#inputGroupSelect01');
        for(i=1; i<=candidateCounts ;i++){
            CInfo = await getCandiInfo(i);
            optrow.innerHTML += "<option  value= " + CInfo['id']+ " > " + CInfo['name'] + "</option>";
        }
        res.send(indexfile.serialize());
    
    }catch(err){
        console.log(err);
        file = fs.readFileSync(__dirname + "/error.html")
        errorfile = new JSDOM(file)
        errorfile.window.document.getElementById("errorMessage").innerHTML = err
        res.send(errorfile.serialize());
        
    }    
})

app.get('/results', async function(req , res){
    try{ 
        selectedCandi =req.query.SelectedID;
        console.log('selected candidate ',selectedCandi);
        VoterAddr =req.query.VotAdd;
        console.log('voter id',VoterAddr);
        
        if (selectedCandi == undefined | VoterAddr== undefined){

            candidateCounts = await candidateCount();                
            // console.log( 'candi info', CInfo['0'][0])
            file =fs.readFileSync(__dirname + '/result.html' , 'utf-8');       
            resultfile = new JSDOM(file);
            candirow = resultfile.window.document.querySelector('#nicetable #allcandi');
            for(i=1; i<=candidateCounts;i++){
                CInfo = await getCandiInfo(i);
                candirow.innerHTML += "<tr><td style=padding-left:90px;> "+ " " + CInfo['name'] + "</td> <td style=padding-left:90px;> " + CInfo['VC'] +"</td></tr>";
            }
        res.send(resultfile.serialize());
        }else{
        
        candidateCounts = await candidateCount();     
        var result2 = await dovote(selectedCandi,VoterAddr); 
        console.log( 'voted --> ',result2  );
        
        console.log(' \\n ');
        
        var totalvot = await ElectionInstance.totalVotess();
        console.log('total votes are', totalvot['words'][0]);
        console.log(' \\n ');
        // console.log( 'candi info', CInfo['0'][0])
        file =fs.readFileSync(__dirname + '/result.html' , 'utf-8');       
        resultfile = new JSDOM(file);
        s = resultfile.window.document.querySelector('#selection')
        s.innerHTML += "You voted for Candidate <b >" +selectedCandi +"</b>"

        candirow = resultfile.window.document.querySelector('#nicetable #allcandi');
        for(i=1; i<=candidateCounts;i++){
            CInfo = await getCandiInfo(i);
            candirow.innerHTML += "<tr><td style=padding-left:90px;> "+ " " + CInfo['name'] + "</td> <td style=padding-left:90px;> " + CInfo['VC'] +"</td></tr>";
        }
        res.send(resultfile.serialize());
        // res.sendFile(__dirname + '/result.html')
        }
    }catch(err){
        console.log(err);
        file = fs.readFileSync(__dirname + "/error.html")
        errorfile = new JSDOM(file)
        errorfile.window.document.getElementById("errorMessage").innerHTML = err
        res.send(errorfile.serialize());

    } 
})



