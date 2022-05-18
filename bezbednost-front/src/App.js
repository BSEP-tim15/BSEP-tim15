import './App.css';
import LogIn from './components/LogIn';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Certificates from './components/admin/Certificates';
import { ToastProvider } from "react-toast-notifications";
import UserProfile from './components/UserProfile';
import IntermediateCertificates from './components/intermediate/IntermediateCertificates';
import EndEntityCertificates from './components/endEntity/EndEntityCertificates';
import RootCertificates from './components/root/rootCertificates';
import ForgotPassword from './components/ForgotPassword';
import Verification from './components/Verification';
import ResetPassword from './components/ResetPassword';
import ChangePassword from './components/ChangePassword';

function App() {
  return (
    <ToastProvider autoDismiss={true}>
      <Router>
        <div className="App">
          <Routes>
            <Route exact path="/" element={<LogIn/>} />
            <Route exact path="/profile" element={<UserProfile/>} />
            <Route exact path="/certificates" element={<Certificates/>} />
            <Route exact path="/rootCertificates" element={<RootCertificates/>} />
            <Route exact path="/intermediateCertificates" element={<IntermediateCertificates/>} />
            <Route exact path="/endEntityCertificates" element={<EndEntityCertificates/>} />
            <Route exact path="/forgotPassword" element={<ForgotPassword/>} />
            <Route exact path="/verify/:code"element={<Verification/>} />
            <Route exact path="/resetPassword/:token" element={<ResetPassword/>}/>
            <Route exact path="/changePassword" element={<ChangePassword/>} />
          </Routes>
        </div>
      </Router>
    </ToastProvider>
  );
}

export default App;
