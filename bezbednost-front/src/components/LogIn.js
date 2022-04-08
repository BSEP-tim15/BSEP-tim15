import { Link } from 'react-router-dom';
import '../App.css'
import { useNavigate } from "react-router-dom";
import { useState } from 'react';
import axios from 'axios';
import { useToasts } from 'react-toast-notifications';
import Passwords from './modals/Passwords';

const LogIn = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const { addToast } = useToasts();
    const [passwordsModal, setPasswordsModal] = useState(false);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const credentials = {
        username,
        password
    }

    const logIn = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/auth/login", credentials)
            .then(response => {
                let token = response.data.accessToken;
                console.log(token);
                localStorage.setItem('jwtToken', token);
                setPasswordsModal(true);
            })
            .catch(error => {
                addToast("Wrong username or password. Please try again.", { appearance: "error" });
            });
        
    }

    return (
        <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
            <div className='card-body'>
                <h4 className='card-title'>Log in</h4>
                <div className='title-underline'/>
                <form className='mt-4 text-start' onSubmit={(e) => logIn(e)}>
                    <label className='form-label'>Username</label>
                    <input className='form-control' type="text" placeholder='Enter username' value={username} onChange={(e) => setUsername(e.target.value)} />
                    <label className='form-label mt-2'>Password</label>
                    <input className='form-control' type="password" placeholder='Enter password' value={password} onChange={(e) => setPassword(e.target.value)} />
                    <button type='submit' className='btn mt-4 w-25' 
                        style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                        Log in
                    </button>
                </form>
            </div>

            <Passwords modalIsOpen={passwordsModal} setModalIsOpen={setPasswordsModal} credentials={credentials} />
        </div>
    )

}

export default LogIn;