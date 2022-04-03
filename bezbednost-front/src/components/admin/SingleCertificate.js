import { useEffect, useState } from 'react';
import Modal from 'react-modal';
import { useToasts } from "react-toast-notifications";
import axios from "axios";
import { format } from 'date-fns';
import print from "../../images/print.png";

const SingleCertificate = ({modalIsOpen, setModalIsOpen, serialNumber}) => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const {addToast} = useToasts();

    const [certificate, setCertificate] = useState({});

    useEffect(() => {
        axios.get(SERVER_URL + "/certificates/certificate?serialNumber=" + serialNumber)
            .then(response => {
                var certificate = response.data;
                certificate.subject = certificate.subject.substring(3);
                certificate.issuer = certificate.issuer.substring(3);
                certificate.validFrom = format(certificate.validFrom, 'dd.MM.yyyy.');
                certificate.validTo = format(certificate.validTo, 'dd.MM.yyyy.');
                setCertificate(certificate);
            })

    }, [modalIsOpen])

    const exportCertificate = (e) => {
        e.preventDefault();
        axios.post(SERVER_URL + "/certificates/exportCertificate?serialNumber=" + serialNumber)
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
                            <input type="text" className='form-control' required value={serialNumber} disabled/>
                            <label className='form-label mt-3'>Subject</label>
                            <input type="text" className='form-control' required value={certificate.subject} disabled/>
                            <label className='form-label mt-3'>Issuer</label>
                            <input type="text" className='form-control' required value={certificate.issuer} disabled/>
                            <label className='form-label mt-3'>Valid from</label>
                            <input type="text" className='form-control' required value={certificate.validFrom} disabled/>
                            <label className='form-label mt-3'>Valid to</label>
                            <input type="text" className='form-control' required value={certificate.validTo} disabled/>
                            
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