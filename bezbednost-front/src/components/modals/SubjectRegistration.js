import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";
import { validName, validCountry, validEmail, validPassword } from '../../validation/SubjectValidation';

const SubjectRegistration = ({modalIsOpen, setModalIsOpen, username, role}) => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const [subject, setSubject] = useState({
        name: "",
        username: "",
        password: "",
        country: "",
        email: "",
        role: ""
    })

    const registerSubject = (e) => {
        e.preventDefault();
        if (validate()) {
            var newSubject = {...subject, username:username, role:role}
            axios.post(SERVER_URL + "/users", newSubject)
            //axios.post(SERVER_URL + "/auth/signup", newSubject)
                .then(response => {
                    setModalIsOpen(false);
                    window.location.reload();
            })
        }
    }

    const validate = () => {
        if (!validName.test(subject.name)) {
            alert("Invalid name!");
            return false;
        }
        if (!validCountry.test(subject.country)) {
            alert("Invalid country name!");
            return false;
        }
        if (!validEmail.test(subject.email)) {
            alert("Invalid email!");
            return false;
        }
        if(!validPassword.test(subject.password)){
            alert("Invalid password!");
            return false;
        }
        return true;
    }

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card w-50 mb-5' 
                    style={{marginLeft: "25%", maxHeight: "600px", marginTop: "4%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>Register subject</h4>
                        <div className='title-underline'/>
                        <form className='mt-4' onSubmit={(e) => registerSubject(e)}>
                            <label className='form-label mt-0'>Name</label>
                            <input className='form-control' type="text" required
                                value={subject.name} onChange={(e) => setSubject(() => {return {...subject, name: e.target.value}})}/>
                            <label className='form-label mt-2'>Username</label>
                            <input className='form-control' type="text" required disabled value={username}/>
                            <label className='form-label mt-2'>Password</label>
                            <input className='form-control' type="password" required
                                value={subject.password} onChange={(e) => setSubject(() => {return {...subject, password: e.target.value}})}/>
                            <label className='form-label mt-2'>Email</label>
                            <input className='form-control' type="text" required
                                value={subject.email} onChange={(e) => setSubject(() => {return {...subject, email: e.target.value}})}/>                          
                            <label className='form-label mt-2'>Country</label>
                            <input className='form-control' type="text" required
                                value={subject.country} onChange={(e) => setSubject(() => {return {...subject, country: e.target.value}})}/>
                            
                            <button type='submit' className='btn mt-5 w-25' 
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

export default SubjectRegistration;