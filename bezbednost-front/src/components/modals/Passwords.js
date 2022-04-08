import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useNavigate } from 'react-router-dom';
import { useToasts } from 'react-toast-notifications';
import axios from 'axios';

const Passwords = ({modalIsOpen, setModalIsOpen, credentials}) => {

    const navigate = useNavigate();

    const [passwords, setPasswords] = useState({root: "", intermediate: "", endEntity: ""});

    useEffect(() => {

        setPasswords({root: "", intermediate: "", endEntity: ""});

    }, [])

    const submitPasswords = (e) => {
        e.preventDefault();
        localStorage.setItem('rootPassword', passwords.root);
        localStorage.setItem('intermediatePassword', passwords.intermediate);
        localStorage.setItem('endEntityPassword', passwords.endEntity);
        setModalIsOpen(false);
        navigate("/certificates");
        
    }

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card' 
                    style={{marginLeft: "32%", width: "35%", marginTop: "7%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>Passwords</h4>
                        <div className='title-underline'/>
                        <form onSubmit={(e) => submitPasswords(e)}>
                            <label className='form-label mt-5'>Password for file with <b>root certificates</b></label>
                            <input className='form-control' type="password" required 
                                value={passwords.root} onChange={(e) => setPasswords(() => {return {...passwords, root: e.target.value}})} />
                            <label className='form-label mt-3'>Password for file with <b>intermediate certificates</b></label>
                            <input className='form-control' type="password" required 
                                value={passwords.intermediate} onChange={(e) => setPasswords(() => {return {...passwords, intermediate: e.target.value}})} />
                                <label className='form-label mt-3'>Password for file with <b>end-entity certificates</b></label>
                            <input className='form-control' type="password" required 
                                value={passwords.endEntity} onChange={(e) => setPasswords(() => {return {...passwords, endEntity: e.target.value}})} />
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

export default Passwords;