pragma solidity ^0.6.4;
// pragma solidity ^0.5.0;

contract Election {
    struct Voter {
        address Sender;
        string Name;
        // Constituency Code
        uint32 Constituency;
        // Has User Voted
        bool HasVoted;
    }

    struct Candidate {
        address sender;
        string name;
        // Constituency Code
        uint32 constituency;
    }

    mapping(uint32 => Candidate) public candidates;
    mapping(uint32 => uint32) votesForCandidate;
    mapping(address => Voter) public voters;
    uint32 maxCandidates;
    // Adding Election Officers as Centralized Authority
    mapping(address => bool) electionOfficer;

    modifier CheckIfChair() {
        require(electionOfficer[msg.sender], "Election Officer Can Only Perform Operation");
        _;
    }

    constructor() public {
        maxCandidates = 0;
        electionOfficer[msg.sender] = true;
    }

    function AddElectionOfficer(address newElectionOfficer) public CheckIfChair{
        electionOfficer[newElectionOfficer] = true;
    }

    function MaxCandidate() public view returns (uint32) {
        return maxCandidates;
    }

    function RegisterCandidate(
        address candidateAddr,
        string memory name,
        uint32 constituency
    ) public CheckIfChair {
        for (uint32 i = 0; i < maxCandidates; ++i)
            require (candidates[i].sender != candidateAddr, "Candidate with Same Address Found");
        candidates[maxCandidates] = Candidate(
            candidateAddr,
            name,
            constituency
        );
        votesForCandidate[maxCandidates] = 0;
        ++maxCandidates;
    }

    function RegisterVoter(
        address voterAddr,
        string memory name,
        uint32 constituency
    ) public CheckIfChair {
        Voter memory currentVoter = voters[voterAddr];
        require(currentVoter.Sender == address(0), "Voter Already Exists");
        voters[voterAddr] = Voter(voterAddr, name, constituency, false);
    }

    function VoteFor(uint32 candidateSupported) public {
        Voter memory currentVoter = voters[msg.sender];
        Candidate memory selectedCandidate = candidates[candidateSupported];
        require(
            !currentVoter.HasVoted &&
                currentVoter.Constituency == selectedCandidate.constituency,
            "Not in Same Constituency"
        );
        currentVoter.HasVoted = true;
        votesForCandidate[candidateSupported]++;
    }

    function Winner(uint32 constituency) public view returns (string memory) {
        uint32 selectedCandidate = 0;
        require(maxCandidates != 0, "No Candidates Added");
        for (uint32 i = 0; i < maxCandidates; ++i) {
            Candidate memory currentCandidate = candidates[i];
            if (currentCandidate.constituency == constituency) {
                uint32 votesRecv = votesForCandidate[i];
                if (votesRecv > votesForCandidate[selectedCandidate])
                    selectedCandidate = i;
            }
        }
        return candidates[selectedCandidate].name;
    }
}