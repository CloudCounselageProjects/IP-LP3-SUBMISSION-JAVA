pragma solidity ^0.5.16;

contract Election{

    struct candidate{
        uint id;
        string party;
        string name;
        uint VC;
    }

    struct voter{
        uint id;
        address addr;
        string name;
        bool authorised;
    }
    // creating a table to store all candidates
    mapping (uint => candidate) public candidates;
    candidate[] public candidat;

    //table for voters
    mapping (uint => address ) public VAdress;
    mapping (address => voter) public voters;

    // creating a table to cheak if person voted or not
    mapping (address => bool) public votedORnot;

    uint public candidateCount;
    uint public voterCount;
    uint private totalVotes;

    address public owner;

    modifier _ownerOnly(){

        require(msg.sender == owner,'You are not an ADMIN');
        _;
    }
    //Constructor i.e. executes only at time of deployent

    constructor() public{
        owner = msg.sender;
        addCandidate("candidate 1","MNS");
        addCandidate("candidate 2","ShivSena");
        addCandidate("candidate 3","BJP");
        addCandidate("candidate 4","RCP");
        addCandidate("candidate 5","AAP");
    }


    function addCandidate(string memory name,string memory party) private _ownerOnly {
        candidateCount++;
        candidat.push(candidate(candidateCount,party,name,0));
        candidates[candidateCount] = candidate(candidateCount,party,name,0);

    }

    function authoriseVoter (address addr, string memory name) public _ownerOnly {
        voterCount++;
        VAdress[voterCount] = addr;
        voters[addr] = voter(voterCount,addr, name,true);

    }
    // function authoriseVoter (address add) public  {
    //     voterCount++;
    //     voters[add] = voter(voterCount,true);

    // }

    function vote_candidate( uint cid,address add) public{
        // voting will only start if, there are more than 1000 voters
        // require(voterCount>=1000,'voting will only start if, there are more than 1000 voters');

        require(votedORnot[add] == false,'You have Already voted');

        require(voters[add].authorised == true,'You are not authorised . Please Contact Admin');

        require(cid > 0 && cid <= candidateCount,'Please select correct candidate');

        candidates[cid].VC += 1;
        totalVotes++;
        votedORnot[msg.sender] = true;
    }

    function totalVotess( ) external view returns(uint){
        return totalVotes;

    }

}

