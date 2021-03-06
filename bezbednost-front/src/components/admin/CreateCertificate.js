import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";
import RegistrationQuestion from '../modals/RegistrationQuestion';
import { containsDangerousCharacters, validName } from '../../validation/CertificateValidation';

const CreateCertificate = ({modalIsOpen, setModalIsOpen}) => {

    const SERVER_URL = process.env.REACT_APP_API; 
    const {addToast} = useToasts();

    const [registration, setRegistration] = useState(false);

    const [minDate, setMinDate] = useState("");

    const certificateTypes = ["root", "intermediate", "end-entity"];
    const [issuers, setIssuers] = useState([]);
    const [type, setType] = useState("");
    const [certificate, setCertificate] = useState({
        certificateType: "",
        issuer: "",
        subject: "",
        validFrom : new Date(),
        validTo: new Date(),
        issuerAlternativeName: "",
        subjectAlternativeName:""
    });
    

    useEffect(() => {

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
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}
        setCertificate(() => {return {...certificate, certificateType: type}});
        setType(type);

        var passwords = {
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        axios.post(SERVER_URL + "/certificates/issuers", passwords, {headers: headers})
            .then(response => {
                setIssuers(response.data);
            })
    }

    const setIssuer = (issuer) => {
        setCertificate(() => {return {...certificate, issuer: issuer}});
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}

        if(type !== "root"){

            var certificate = {
                someone: issuer, 
                rootPassword: localStorage.rootPassword, 
                intermediatePassword: localStorage.intermediatePassword, 
                endEntityPassword: localStorage.endEntityPassword
            }

            axios.post(SERVER_URL + "/certificates/maxDate", certificate, {headers: headers})
                .then(response => {
                    
                    var maxDate = response.data;
                    maxDate = generateDate(new Date(maxDate));
                    document.getElementById("validTo").max = maxDate;
                })
        }
    }

    const createCertificate = (e) => {
        e.preventDefault();
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}

        if(isCertificateValid()){

            console.log(type);

            var cert = {
                certificateType: type,
                issuer: certificate.issuer,
                subject: certificate.subject,
                validFrom : certificate.validFrom,
                validTo: certificate.validTo,
                issuerAlternativeName: certificate.issuerAlternativeName,
                subjectAlternativeName:certificate.subjectAlternativeName,
                rootPassword: localStorage.rootPassword,
                intermediatePassword: localStorage.intermediatePassword,
                endEntityPassword: localStorage.endEntityPassword,
            };
            

            axios.post(SERVER_URL + "/certificates/createCertificate", cert, {headers: headers})
                .then(response => {

                    axios.get(SERVER_URL + "/users/isUserRegistered?username=" + certificate.subject, {headers:headers})
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

    }

    const isCertificateValid = () => {
        if(new Date(certificate.validFrom) >= new Date(certificate.validTo)){
            addToast("Valid from date has to be before valid to date!", {appearance : "error"});
            return false;
        } 
        if(type === "root"){
            if(certificate.issuer !== certificate.subject){
                addToast("For root (self signed) certificates subject and issuer have to be same!", {appearance : "error"});
                return false;
            }
        }
        if (!validName.test(certificate.issuer) || containsDangerousCharacters(certificate.issuer)) {
            alert("Invalid issuer name!");
            return false;
        }
        if (!validName.test(certificate.subject) || containsDangerousCharacters(certificate.subject)) {
            alert("Invalid subject name!");
            return false;
        }
        if (!validName.test(certificate.issuerAlternativeName) || containsDangerousCharacters(certificate.issuerAlternativeName)) {
            alert("Invalid subject alternative name!");
            return false;
        }
        if (!validName.test(certificate.subjectAlternativeName) || containsDangerousCharacters(certificate.subjectAlternativeName)) {
            alert("Invalid issuer alternative name!");
            return false;
        }
        return true;
    }

    const issuerForm = (
        <div>
            {certificate.certificateType === "root" ? (
                <input className='form-control' type="text" required
                    value={certificate.issuer} onChange={(e) => setCertificate(() => {return {...certificate, issuer: e.target.value}})}/>
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
                            <input className='form-control' type="text" required 
                                value={certificate.subject} onChange={(e) => setCertificate(() => {return {...certificate, subject: e.target.value}})} />
                            <label className='form-label mt-3'>Valid from</label>
                            <input className='form-control' type="date" required min={minDate}
                                value={certificate.validFrom} onChange={(e) => setCertificate(() => {return {...certificate, validFrom: e.target.value}})}/>
                            <label className='form-label mt-3'>Valid to</label>
                            <input id="validTo" className='form-control' type="date" required min={minDate}
                                value={certificate.validTo} onChange={(e) => setCertificate(() => {return {...certificate, validTo: e.target.value}})}/>
                            <label className='form-label mt-3'>Issuer alternative name</label>
                            <textarea className='form-control' type="text"  
                                value={certificate.subjectAlternativeName} onChange={(e) => setCertificate(() => {return {...certificate, subjectAlternativeName: e.target.value}})}/>
                            <label className='form-label mt-3'>Subject alternative name</label>
                            <textarea className='form-control' type="text"  
                                value={certificate.issuerAlternativeName} onChange={(e) => setCertificate(() => {return {...certificate, issuerAlternativeName: e.target.value}})}/>                                           
                            <button type='submit' className='btn mt-4 w-25' 
                                style={{marginLeft: "35%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                                Submit
                            </button>
                        </form>
                    </div>
                </div>
            </Modal>

            <RegistrationQuestion modalIsOpen={registration} setModalIsOpen={setRegistration} username={certificate.subject} role={type} />
        </div>
    )

}

export default CreateCertificate;