import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useToasts } from 'react-toast-notifications';
import Passwords from './modals/Passwords';

const PasswordlessLogin = () => {

    const SERVER_URL = process.env.REACT_APP_API;
    const url = window.location.href;
    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [isTokenValid, setIsTokenValid] = useState(false);
    const [passwordsModal, setPasswordsModal] = useState(false);

    useEffect( () => {
        const splitted = url.split("/");
        axios.get(SERVER_URL + "/users/validatePasswordToken?" + splitted[splitted.length-1])
        .then(response => {
            if(response.status == 204) {
                setIsTokenValid(false);
                setMessage("Your token has expired, please request new!");
            } else if(response.status == 200) {
                setIsTokenValid(true);
                setMessage("You are successfully logged in");
                axios.post(SERVER_URL + "/auth/loginPasswordless?" + splitted[splitted.length-1])
                .then(response => {
                    let token = response.data.accessToken;
                    console.log(token);
                    localStorage.setItem('jwtToken', token);
                    setPasswordsModal(true);
                })
            }
        })
    }, [])

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
        <div className='card' style={{"width": "55%", "marginTop": "10%", "marginLeft": "25%"}} >
            <h1>{message}</h1>
            <Passwords modalIsOpen={passwordsModal} setModalIsOpen={setPasswordsModal} />
        </div>
    )
}

export default PasswordlessLogin;