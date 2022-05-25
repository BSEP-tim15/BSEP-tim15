import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useToasts } from 'react-toast-notifications';
import { validPassword } from "../validation/SubjectValidation";

const ResetPassword = () => {

    const SERVER_URL = process.env.REACT_APP_API;
    const url = window.location.href;

    const { addToast } = useToasts();
    const navigate = useNavigate();

    const [token, setToken] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirmation, setPasswordConfirmation] = useState("");
    const [message, setMessage] = useState("");
    const [isTokenValid, setIsTokenValid] = useState(false);
    const [messagePass, setMessagePass] = useState("");
    const [cleanToken, setCleanToken] = useState("");

    const changePasswordDto = {
        password: password,
        token: cleanToken
    }

    useEffect( () => {
        const splitted = url.split("/");
        axios.get(SERVER_URL + "/users/validateToken?" + splitted[splitted.length-1])
        .then(response => {
            if(response.status == 204) {
                setIsTokenValid(false);
                setMessage("Your token has expired, please request new!");
            } else if(response.status == 200) {
                setIsTokenValid(true);
                parseToken(splitted[splitted.length-1]);
            }
        })
    }, [])

    const parseToken = (token) => {
        const splitted = token.split("=");
        setCleanToken(splitted[splitted.length-1]);
    }

    const resetPassword = (e) => {
        e.preventDefault();
        if(!passwordMatches()) return
        if(validate()) {
            axios.put(SERVER_URL + "/users/resetPassword", changePasswordDto)
                .then(
                    response => {
                        if(response.status == 204) {
                            setIsTokenValid(false);
                            setMessage("Your token has expired, please request new!");
                        } else {
                            addToast("Your password is successfully changed!", { appearance: "success" });
                            navigate("/");
                        }
                    }
                )
            }
        
    }

    const passwordMatches = () => {
        if(password === passwordConfirmation){
            setMessagePass("")
            return true
        }
        setMessagePass("Passwords do not match!")
        return false
    }

    const validate = () => {
        if(!validPassword.test(password)){
            alert("Invalid password!");
            return false;
        }
        return true;
    }

    return (
        <div>
        { isTokenValid && 
            <div className='card' style={{"width": "50%", "marginTop": "10%", "marginLeft": "25%"}} >
                <div className='card-body'>
                <h4 className='card-title'>Reset password</h4>
                    <div className='title-underline'/>
                    <form className='mt-4 text-start' onSubmit={(e) => resetPassword(e)}>
                        <label className='form-label'>New password:</label>
                        <input className='form-control' type="password" required placeholder='Enter password' value={password} onChange={(e) => setPassword(e.target.value)} /> <br />
                        <label className='form-label'>Confirm new password:</label>
                        <input className='form-control' type="password" required placeholder='Enter password' value={passwordConfirmation} onChange={(e) => setPasswordConfirmation(e.target.value)} />
                        <p style={{color: "red"}}>{messagePass}</p>
                        <button type='submit' className='btn mt-4 w-25' 
                            style={{marginLeft: "38%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                            Reset password
                        </button>
                    </form>
                </div>
            </div>
        }

        { !isTokenValid &&
            <div className='card' style={{"width": "55%", "marginTop": "10%", "marginLeft": "25%"}} >
                <h1>{message}</h1>
            </div>
        }

        </div>
    )
}

export default ResetPassword;