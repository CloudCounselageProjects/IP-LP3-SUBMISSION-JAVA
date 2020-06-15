pragma solidity >=0.4.25;
pragma experimental ABIEncoderV2;


contract OLVoteSys {
    // Global Variables

    struct Candidate {
        string name;
        string represent;
        uint256 age;
        uint256 noOfVotes;
    }

    mapping(uint256 => Candidate) public allCandidates;
    mapping(uint256 => address) public voters;

    uint256 noOfCandidates = 0;
    uint256 noOfVoters = 0;

    // Methods

    function registerCandidate(
        string memory _name,
        string memory _represent,
        uint256 _age
    ) public {
        allCandidates[noOfCandidates].name = _name;
        allCandidates[noOfCandidates].represent = _represent;
        allCandidates[noOfCandidates].age = _age;
        allCandidates[noOfCandidates].noOfVotes = 0;
        noOfCandidates++;
    }

    function registerVoter(address _voterAddress) public returns (uint256) {
        voters[noOfVoters++] = _voterAddress;
        return noOfVoters;
    }

    function vote(uint256 to) public {
        allCandidates[to].noOfVotes++;
    }

    function getResultTillNow(uint256 i) public view returns (uint256) {
        return allCandidates[i].noOfVotes;
    }

    function getNoOfCandidates() public view returns (uint256) {
        return noOfCandidates;
    }

    function getNoOfVoters() public view returns (uint256) {
        return noOfVoters;
    }

    function getVoterAddress(uint id) public view returns (address) {
        return voters[id];
    }

    function showCandidate(uint id) public view returns (Candidate memory) {
        return allCandidates[id];
    }
}


contract Voter {
    address consensus;
    string name;
    string IDProof;
    uint256 age;
    bool hasVoted;
    string password;
    int votedTo = -1;

    mapping(uint256 => address) public allVoters;
    uint256 noOfVoters = 0;

    constructor(
        address _OLVoteSys,
        string memory _name,
        string memory _IDProof,
        string memory _password,
        uint256 _age
    ) public {
        consensus = _OLVoteSys;
        name = _name;
        IDProof = _IDProof;
        password = _password;
        age = _age;
        hasVoted = false;
    }

    function vote(int to) public {
        votedTo = to;
        hasVoted = true;
    }

    function isEligible() public view returns (bool) {
        if (age <= 18) {
            return false;
        } else if (hasVoted == true) {
            return false;
        } else {
            return true;
        }
    }

    function getVoterDetails() public view returns (string memory, string memory, uint, int, string memory) {
        return (name, IDProof, age, votedTo, password);
    }
}
