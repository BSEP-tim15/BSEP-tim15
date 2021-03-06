import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";
import { format } from 'date-fns';
import print from "../../images/print.png";

const SingleCertificate = ({modalIsOpen, setModalIsOpen, serialNumber, setSerialNumber}) => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const {addToast} = useToasts();

    const [certificate, setCertificate] = useState({});

    useEffect(() => {

        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}

        var certificate = {
            serialNumber: serialNumber, 
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        axios.post(SERVER_URL + "/certificates/singleCertificate", certificate, {headers: headers})
            .then(response => {
                var certificate = response.data;
                certificate.subject = certificate.subject.substring(3);
                certificate.issuer = certificate.issuer.substring(3);
                certificate.validFrom = format(certificate.validFrom, 'dd.MM.yyyy.');
                certificate.validTo = format(certificate.validTo, 'dd.MM.yyyy.');
                setCertificate(certificate);
            })

    }, [serialNumber])

    const getSerialNumberOfParentCertificate = () => {

        var certificate = {
            serialNumber: serialNumber,
            rootPassword: localStorage.rootPassword,
            intermediatePassword: localStorage.intermediatePassword,
            endEntityPassword: localStorage.endEntityPassword
        }

        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}

        axios.post(SERVER_URL + "/certificates/parentCertificateSerialNumber", certificate, {headers: headers})
            .then(response => {
                setSerialNumber(response.data);
            })
    }

    const exportCertificate = (e) => {
        const headers = {'Content-Type' : 'application/json', 'Authorization' : `Bearer ${localStorage.jwtToken}`}

        e.preventDefault();

        var certificate = {
            serialNumber: serialNumber, 
            rootPassword: localStorage.rootPassword, 
            intermediatePassword: localStorage.intermediatePassword, 
            endEntityPassword: localStorage.endEntityPassword
        }

        axios.post(SERVER_URL + "/certificates/exportCertificate", certificate, {headers: headers})
            .then(response => {
                addToast("Certificate is successfully exported!", { appearance: "success" });
                setTimeout(() => {
                    setModalIsOpen(false);
                }, 2500);
            })
    }

    return (
        <div>
            <Modal className="fullscreen" isOpen={modalIsOpen} shouldCloseOnEsc={true} onRequestClose={() => setModalIsOpen(false) } ariaHideApp={false}>
                <div className='card w-50 mb-5' 
                    style={{marginLeft: "25%", maxHeight: "600px", marginTop: "4%", borderColor: "#4a6560"}}>
                    <button className="btn-close ms-auto mt-2 me-2" onClick={() => setModalIsOpen(false)}/>
                    <div className='card-body' style={{overflowY: "scroll"}}>
                        <h4 className='card-title' style={{marginTop: "-20px"}}>Certificate: {serialNumber}</h4>
                        <div className='title-underline'/>
                        <form className='mt-4' onSubmit={(e) => exportCertificate(e)}>
                            <label className='form-label mt-2'>Serial number</label>
                            <input type="text" className='form-control' value={serialNumber} disabled/>
                            <label className='form-label mt-3'>Certificate type</label>
                            <input type="text" className='form-control' value={certificate.certificateType} disabled/>
                            <label className='form-label mt-3'>Subject</label>
                            <input type="text" className='form-control' value={certificate.subject} disabled/>
                            <label className='form-label mt-3'>Issuer</label>
                            <input type="text" className='form-control' value={certificate.issuer} disabled/>
                            <label className='form-label mt-3'>Valid from</label>
                            <input type="text" className='form-control' value={certificate.validFrom} disabled/>
                            <label className='form-label mt-3'>Valid to</label>
                            <input type="text" className='form-control' value={certificate.validTo} disabled/><br/>
                            <label className='form-label'>Issuer alternative name</label>
                            <input type="text" className='form-control' value={certificate.issuerAlternativeName} disabled/><br/>
                            <label className='form-label'>Subject alternative name</label>
                            <input type="text" className='form-control' value={certificate.subjectAlternativeName} disabled/><br/>
                            {certificate.certificateType != "root" && (
                                <div>
                                    <a style={{color: "blue", textDecoration: "underline"}} onClick={() => getSerialNumberOfParentCertificate()}>View parent certificate</a><br/>
                                </div>
                            )}
                            
                            <button type='submit' className='btn mt-5 w-25' 
                                style={{marginLeft: "35%", backgroundColor: "#4a6560", color: "white", borderRadius: "12px"}}>
                                <img className='icon me-2' src={print} />
                                Export
                            </button>

                        </form>
                    </div>
                </div>
            </Modal>

        </div>
    )

}

export default SingleCertificate;