import { useEffect, useState } from "react";
import axios from "axios";
import { format } from 'date-fns';
import NavBar from "../NavBar";
import validImg from "../../images/valid.png";
import invalidImg from "../../images/invalid.png";
import SingleCertificate from "./SingleCertificate";

const Certificates = () => {

    const SERVER_URL = process.env.REACT_APP_API; 

    const [certificateType, setCertificateType] = useState("");
    const [certificates, setCertificates] = useState([]);
    const [showCertificate, setShowCetificate] = useState(false);
    const [serialNumber, setSerialNumber] = useState(0);

    useEffect(() => {
        
        axios.get(SERVER_URL + "/certificates?certificateType=" + certificateType)
            .then(response => {
                setCertificates(response.data);
            })

    }, [certificateType])

    const showSingleCertificate = (serialNumber) => {
        setSerialNumber(serialNumber);
        setShowCetificate(true);
    }

    const allCertificates = (
        certificates.map(certificate => (
            <tr key={certificate.serialNumber} onClick={() => showSingleCertificate(certificate.serialNumber)}>
                <td>{certificate.serialNumber}</td>
                <td>{certificate.subject.substring(3)}</td>
                <td>{certificate.issuer.substring(3)}</td>
                <td>{format(certificate.validFrom, 'dd.MM.yyyy.')}</td>
                <td>{format(certificate.validTo, 'dd.MM.yyyy.')}</td>
                <td><img className="icon ms-2" src={`${certificate.valid ? validImg : invalidImg}`}/></td>
            </tr>
        ))
    )

    return (
        <div>
            <NavBar/>
            <div className='card' style={{marginLeft: "5%", width: "90%", maxHeight: "550px", marginTop: "1%", borderColor: "#4a6560"}}>
                <div className='card-body' style={{overflowY: "scroll"}}>
                    <h4 className='card-title'>Certificates</h4>
                    <div className='title-underline'/>
                    <div style={{display: "flex"}}>
                        <span className="mt-4">Filter by type:</span>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("root")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Root
                        </button>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("intermediate")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Intermediate
                        </button>
                        <button className='btn mt-3 ms-3' onClick={() => setCertificateType("end-entity")}
                            style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            End-entity
                        </button>
                    </div>
                    <table className="table mt-4">
                        <thead>
                            <tr>
                                <th style={{width: "18%"}}>Certificate ID</th>
                                <th style={{width: "18%"}}>Subject</th>
                                <th style={{width: "18%"}}>Issuer</th>
                                <th style={{width: "18%"}}>Valid from</th>
                                <th style={{width: "18%"}}>Valid to</th>
                                <th style={{width: "18%"}}>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {allCertificates}
                        </tbody>
                    </table>
                </div>
            </div>

            <SingleCertificate modalIsOpen={showCertificate} setModalIsOpen={setShowCetificate} serialNumber={serialNumber} />
        </div>
    )

}

export default Certificates;