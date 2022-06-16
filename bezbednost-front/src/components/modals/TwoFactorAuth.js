import Modal from 'react-modal';
import { useState } from 'react';
import axios from 'axios';
import { useToasts } from 'react-toast-notifications';
import { useNavigate } from 'react-router-dom';

const TwoFactorAuth = ({modalIsOpen, setModalIsOpen, username, password}) => {

    const SERVER_URL = process.env.REACT_APP_API;

    const navigate = useNavigate();
    const { addToast } = useToasts();
    const [code, setCode] = useState("");

    const credentials = {
        username: username,
        password: password,
        code
    }

    const twoFactorLogin = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/auth/tfa-login", credentials)
            .then(response => {
                let token = response.data.accessToken;
                console.log(token);
                localStorage.setItem('jwtToken', token);
                setModalIsOpen(false);
                redirect();
            })
            .catch(error => {
                addToast("Wrong PIN code. Please try again.", { appearance: "error" });
            })
    }

    const redirect = () => {
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
            axios.get(SERVER_URL + "/users", { headers: headers})
            .then(response => {
                var user = response.data;

                axios.get(SERVER_URL + `/users/getRole/${user.id}`, {headers:headers})
                .then(response => {
                    console.log(response.data);
                    var role = response.data;
                    if(role === "admin"){
                        navigate("/certificates");
                    } else if(role === "organization"){
                        navigate("/rootCertificates");
                    } 
                    else if(role === "service"){
                        navigate("/intermediateCertificates");
                    } else {
                        navigate("/endEntityCertificates");
                    }
                });
            });
    }

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card' 
                    style={{marginLeft: "32%", width: "35%", marginTop: "7%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>PIN Code</h4>
                        <div className='title-underline'/>
                        <form onSubmit={(e) => twoFactorLogin(e)}>
                            <label className='form-label mt-5'><b>Please enter your PIN code</b></label>
                            <input className='form-control' type="text" required value={code} onChange={(e) => setCode(e.target.value)} />
                            <button  className='btn mt-5 w-25' 
                                style={{marginLeft: "35%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                                Submit
                            </button>
                        </form>
                    </div>
                </div>
            </Modal>

        </div>
    )

}

export default TwoFactorAuth;