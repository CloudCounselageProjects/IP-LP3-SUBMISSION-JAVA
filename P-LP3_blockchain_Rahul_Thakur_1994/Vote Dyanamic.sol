pragma solidity ^0.5.3;
contract RahulVote{
    address public ContractOwner;
    address[] public candidateList;
    mapping(address=>uint16) public votesReceived;
    address public winner;
    uint public winnerVotes;
    enum ElectionStartStatus{NotStarted,Running,Completed}
    ElectionStartStatus public status;


    constructor()public{
        ContractOwner=msg.sender;
    }
    modifier OnlyRegister{
        if(msg.sender == ContractOwner){
            _;
        }
    }

   function RegisterdCandidates(address _candidates)OnlyRegister public{
       candidateList.push(_candidates);
   }


    function Vote(address _candidates) public{
        require(ValidateCandidate(_candidates),"Not A Valid Candidate");
        require(status==ElectionStartStatus.Running,"Election Not Started");
        votesReceived[_candidates]=votesReceived[_candidates]+1;
    }

   function ValidateCandidate(address _candidates)view public returns(bool){
       for(uint i=0;i<candidateList.length;i++){
           if(candidateList[i]==_candidates){
               return true;
           }
       }
       return false;
   }
    function votesCount(address _candidates)public view returns (uint){
        require(ValidateCandidate(_candidates),"Not A Valid Candidate");
        assert(status==ElectionStartStatus.Running);
        return votesReceived[_candidates];
    }

    function result() public{
        require(status==ElectionStartStatus.Running,"Election Not Finished");
        for(uint i=0;i<candidateList.length;i++){
            if(votesReceived[candidateList[i]] > winnerVotes){
                winnerVotes=votesReceived[candidateList[i]];
                winner=candidateList[i];
            }
        }
    }

    function setStatus()OnlyRegister public{

        if(status!=ElectionStartStatus.Completed){
            status=ElectionStartStatus.Running;
        }
        else{
            status=ElectionStartStatus.Completed;
        }
    }

}
