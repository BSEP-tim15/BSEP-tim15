import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useToasts } from "react-toast-notifications";

const ChangePassword = () => {

    const SERVER_URL = process.env.REACT_APP_API;

    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [passwordConfirmation, setPasswordConfirmation] = useState("");
    const [messagePass, setMessagePass] = useState("");
    const navigate = useNavigate();

    const changePasswordDto = {
        oldPassword: oldPassword,
        newPassword: newPassword
    }

    const changePassword = (e) => {
        e.preventDefault();
        const headers = {'Content-Type' : 'application/json',
                     'Authorization' : `Bearer ${localStorage.jwtToken}`}
        axios.put(SERVER_URL + "/users/changePassword", changePasswordDto, {headers: headers})
            .then(response => {
                navigate("/profile");
            });
    }

    return(
        <div className='card' style={{"width": "50%", "marginTop": "10%", "marginLeft": "25%"}} >
                <div className='card-body'>
                <h4 className='card-title'>Reset password</h4>
                    <div className='title-underline'/>
                    <form className='mt-4 text-start' onSubmit={(e) => changePassword(e)}>
                        <label className='form-label'>Old password:</label>
                        <input className='form-control' type="password" required placeholder='Enter password' value={oldPassword} onChange={(e) => setOldPassword(e.target.value)} /> <br />
                        <label className='form-label'>New password:</label>
                        <input className='form-control' type="password" required placeholder='Enter password' value={newPassword} onChange={(e) => setNewPassword(e.target.value)} /> <br />
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
    )
}

export default ChangePassword;