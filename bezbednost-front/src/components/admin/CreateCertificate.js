import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";
import RegistrationQuestion from './RegistrationQuestion';

const CreateCertificate = ({modalIsOpen, setModalIsOpen}) => {

    const SERVER_URL = process.env.REACT_APP_API; 
    const {addToast} = useToasts();

    const [registration, setRegistration] = useState(false);

    const [maxDate, setMaxDate] = useState("");
    const [minDate, setMinDate] = useState("");

    const certificateTypes = ["root", "intermediate", "end-entity"];
    const [issuers, setIssuers] = useState(["issuer1", "issuer2"]);
    const [certificate, setCertificate] = useState({
        certificateType: "",
        issuer: "",
        subject: "",
        validFrom : new Date(),
        validTo: new Date(),
        purpose: ""
    });
    

    useEffect(() => {

        setMaxDate(generateDate(new Date()));
        setMinDate(generateDate(new Date()));

    }, [])


    const generateDate = (date) => {
        var day = date.getDate();
        var month = date.getMonth()+1;
        var year = date.getFullYear();
        if(getlength(day) === 1) {
            day = "0" + day;
        }
        if(getlength(month) === 1) {
            month = "0" + month;
        }
        return year + '-' + month + '-' + day;
    }

    const getlength = (number) => {
        return number.toString().length;
    }

    const setCertificateType = (type) => {
        setCertificate(() => {return {...certificate, certificateType: type}});
        //GET ISSUERS => setIssuers
    }

    const setIssuer = (issuer) => {
        setCertificate(() => {return {...certificate, issuer: issuer}});
        //GET validTo of choosen issuer's certificate => setMaxDate
    }

    const createCertificate = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/certificates", certificate)
            .then(response => {

                axios.get(SERVER_URL + "/users/isUserRegistered?username=" + certificate.subject)
                    .then(response => {
                        if(response.data === false){
                            setRegistration(true);
                            setModalIsOpen(false);
                        }
                        else{
                            window.location.reload();
                        }
                    })
                
                
            })

    }

    const issuerForm = (
        <div>
            {certificate.certificateType === "root" ? (
                <input className='form-control' type="text" required
                    value={certificate.issuer} onChange={(e) => setIssuer(e.target.value)}/>
            ) : (
                <select className='form-select' required 
                    value={certificate.issuer} onChange={(e) => setIssuer(e.target.value)}>
                    <option></option>
                    {issuers.map(issuer => ( <option key={issuer}>{issuer}</option> ))}
                </select>
            )}
        </div>
    )


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
                                {certificateTypes.map(type => ( <option key={type}>{type}</option> ))}
                            </select>
                            <label className='form-label mt-3'>Issuer</label>
                            {issuerForm}
                            <label className='form-label mt-3'>Subject</label>
                            <input className='form-control' type="text" reqired 
                                value={certificate.subject} onChange={(e) => setCertificate(() => {return {...certificate, subject: e.target.value}})} />
                            <label className='form-label'>Valid from</label>
                            <input className='form-control' type="date" required min={minDate}
                                value={certificate.validFrom} onChange={(e) => setCertificate(() => {return {...certificate, validFrom: e.target.value}})}/>
                            <label className='form-label mt-3'>Valid to</label>
                            <input className='form-control' type="date" required max={maxDate}
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

            <RegistrationQuestion modalIsOpen={registration} setModalIsOpen={setRegistration} username={certificate.subject} role={certificate.certificateType} />
        </div>
    )

}

export default CreateCertificate;