import { useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";

const CreateCertificate = ({modalIsOpen, setModalIsOpen}) => {

    const SERVER_URL = process.env.REACT_APP_API; 
    const {addToast} = useToasts();

    const [disabledEdit, setDisabledEdit] = useState(false);
    const [certificate, setCertificate] = useState({
        certificateType: "",
        issuer: "",
        subject: "",
        validFrom: new Date(),
        validTo: new Date(),
        purpose: ""
    });


    const setCertificateType = (type) => {
        setCertificate(() => {return {...certificate, certificateType: type}});
        if(disabledEdit == true && type != "root") {
            setDisabledEdit(false);
        }
        else if(type == "root") {
            setDisabledEdit(true);
            setCertificate(() => {return {...certificate, certificateType: type, issuer: "root", subject: "root"}});
        }   
    }

    const createCertificate = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/certificates", certificate)
            .then(response => {
                console.log(certificate);
                setModalIsOpen(false);
            })
    }

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card w-50 mb-5' 
                    style={{marginLeft: "25%", maxHeight: "600px", marginTop: "4%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>Create certificate</h4>
                        <div className='title-underline'/>
                        <form className='mt-4' onSubmit={(e) => createCertificate(e)}>
                            <label className='form-label'>Certificate type</label>
                            <select className='form-select' required 
                                value={certificate.certificateType} onChange={(e) => setCertificateType(e.target.value)}>
                                <option></option>
                                <option>root</option>
                                <option>intermediate</option>
                                <option>end-entity</option>
                            </select>
                            <label className='form-label mt-3'>Issuer</label>
                            <input className='form-control' type="text" required disabled={disabledEdit}
                                value={certificate.issuer} onChange={(e) => setCertificate(() => {return {...certificate, issuer: e.target.value}})}/>
                            <label className='form-label mt-3'>Subject</label>
                            <input className='form-control' type="text" required disabled={disabledEdit}
                                value={certificate.subject} onChange={(e) => setCertificate(() => {return {...certificate, subject: e.target.value}})}/>
                            <label className='form-label mt-3'>Valid from</label>
                            <input className='form-control' type="date" required 
                                value={certificate.validFrom} onChange={(e) => setCertificate(() => {return {...certificate, validFrom: e.target.value}})}/>
                            <label className='form-label mt-3'>Valid to</label>
                            <input className='form-control' type="date" required 
                                value={certificate.validTo} onChange={(e) => setCertificate(() => {return {...certificate, validTo: e.target.value}})}/>
                            <label className='form-label mt-3'>Certificate's purpose</label>
                            <textarea className='form-control' type="text" required 
                                value={certificate.purpose} onChange={(e) => setCertificate(() => {return {...certificate, purpose: e.target.value}})}/>
                            <button type='submit' className='btn mt-4 w-25' 
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

export default CreateCertificate;