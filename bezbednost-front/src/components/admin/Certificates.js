import { useEffect, useState } from "react";
import NavBar from "../NavBar";
import validImg from "../../images/valid.png";
import invalidImg from "../../images/invalid.png";

const Certificates = () => {

    const [certificates, setCertificates] = useState([]);

    useEffect(() => {
        setCertificates([
            {
                id: 1,
                issuer: "Mark",
                subject: "Otto",
                validFrom: "22.03.2022.",
                validTo: "22.03.2022.",
                valid: true
            },
            {
                id: 2,
                issuer: "Mark",
                subject: "Otto",
                validFrom: "22.03.2022.",
                validTo: "22.03.2022.",
                valid: false
            },
            {
                id: 3,
                issuer: "Mark",
                subject: "Otto",
                validFrom: "22.03.2022.",
                validTo: "22.03.2022.",
                valid: true
            }
        ])

    }, [])

    const allCertificates = (
        certificates.map(certificate => (
            <tr>
                <td>{certificate.id}</td>
                <td>{certificate.issuer}</td>
                <td>{certificate.subject}</td>
                <td>{certificate.validFrom}</td>
                <td>{certificate.validTo}</td>
                <td><img className="icon" src={`${certificate.valid ? validImg : invalidImg}`}/></td>
            </tr>
        ))
    )

    return (
        <div>
            <NavBar/>
            <div className='card' style={{marginLeft: "5%", width: "90%", marginTop: "1%", borderColor: "#4a6560"}}>
                <div className='card-body' style={{overflowY: "scroll"}}>
                    <h4 className='card-title'>Certificates</h4>
                    <div className='title-underline'/>
                    <div style={{display: "flex"}}>
                        <span className="mt-4">Filter by type:</span>
                        <button className='btn mt-3 ms-3' style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Root
                        </button>
                        <button className='btn mt-3 ms-3' style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            Intermediate
                        </button>
                        <button className='btn mt-3 ms-3' style={{width: "10%", height: "35px", backgroundColor: "#4a6560", color: "white", borderRadius: "20px"}}>
                            End-entity
                        </button>
                    </div>
                    <table className="table mt-4">
                        <thead>
                            <th style={{width: "18%"}}>Certificate ID</th>
                            <th style={{width: "18%"}}>Issuer</th>
                            <th style={{width: "18%"}}>Subject</th>
                            <th style={{width: "18%"}}>Valid from</th>
                            <th style={{width: "18%"}}>Valid to</th>
                            <th style={{width: "18%"}}>Status</th>
                        </thead>
                        <tbody>
                            {allCertificates}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )

}

export default Certificates;