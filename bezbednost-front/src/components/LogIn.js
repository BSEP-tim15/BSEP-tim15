import { Link } from 'react-router-dom';
import '../App.css'
import { useNavigate } from "react-router-dom";
import { useState } from 'react';
import axios from 'axios';
import { useToasts } from 'react-toast-notifications';
import Passwords from './modals/Passwords';
import TwoFALogin from './TwoFALogin';
import TwoFactorAuth from './modals/TwoFactorAuth';

const LogIn = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const { addToast } = useToasts();
    const [passwordsModal, setPasswordsModal] = useState(false);
    const [pinCodeModal, setPinCodeModal] = useState(false);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isPasswordless, setIsPasswordless] = useState(false);
    const [email, setEmail] = useState("");
    const credentials = {
        username,
        password
    }

    const logIn = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/auth/login", credentials)
            .then(response => {
                if (response.data.accessToken === "2fa") {
                    setPinCodeModal(true);
                } else {
                    let token = response.data.accessToken;
                    console.log(token);
                    localStorage.setItem('jwtToken', token);
                    setPasswordsModal(true);
                }
            })
            .catch(error => {
                addToast("Wrong username or password. Please try again.", { appearance: "error" });
                setUsername("");
                setPassword("");
            });
        
    }

    const passwordlessLogin = (e) => {
        axios.get(SERVER_URL + "/users/sendLoginEmail/" + email)
        .then(response => {
            console.log(response.status)
            addToast("Log in link has been sent to your email!", { appearance: "success" });
        })
    }

    return (
        <div>
            {!isPasswordless && 
            <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
                <div className='card-body'>
                    <h4 className='card-title'>Log in</h4>
                    <div className='title-underline'/>
                    <form className='mt-4 text-start' onSubmit={(e) => logIn(e)}>
                        <label className='form-label'>Username</label>
                        <input className='form-control' required type="text" placeholder='Enter username' value={username} onChange={(e) => setUsername(e.target.value)} />
                        <label className='form-label mt-2'>Password</label>
                        <input className='form-control' required type="password" placeholder='Enter password' value={password} onChange={(e) => setPassword(e.target.value)} />
                        <button type='submit' className='btn mt-4 w-25' 
                            style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                            Log in
                        </button> <br />
                        <Link to="/forgotPassword" style={{marginLeft: "41%", fontSize: "13px"}}>Forgot password?</Link> <br />
                        <a onClick={(e)=> setIsPasswordless(true)} style={{marginLeft: "40%", fontSize: "15px", color: "#4a6560"}}>Passwordless login</a> <br />
                    </form>
                </div>
                <Passwords modalIsOpen={passwordsModal} setModalIsOpen={setPasswordsModal} />
                <TwoFactorAuth modalIsOpen={pinCodeModal} setModalIsOpen={setPinCodeModal} username={username} password={password} />
            </div>}
            {isPasswordless &&
            <div className='card ms-auto me-5' style={{width: "40%", "marginTop": "13%", borderColor: "#4a6560"}} >
                <div className='card-body'>
                    <h4 className='card-title'>Log in</h4>
                    <div className='title-underline'/>
                    <form className='mt-4 text-start' onSubmit={(e) => passwordlessLogin(e)}>
                        <label className='form-label'>Email</label>
                        <input className='form-control' required type="text" placeholder='Enter email' value={email} onChange={(e) => setEmail(e.target.value)} />
                        <button type='submit' className='btn mt-4 w-25' 
                            style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                            Log in
                        </button> <br />
                        <a onClick={(e)=> setIsPasswordless(false)} style={{marginLeft: "40%", fontSize: "15px", color: "#4a6560"}}>Back to log in</a> <br />
                    </form>
                </div>
            </div>
            }
        </div>
    )

}

export default LogIn;