import './App.css';
import LogIn from './components/LogIn';
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Certificates from './components/admin/Certificates';
import { ToastProvider } from "react-toast-notifications";
import UserProfile from './components/UserProfile';
import SingleCertificate from './components/admin/SingleCertificate';

function App() {
  return (
    <ToastProvider autoDismiss={true}>
      <Router>
        <div className="App">
          <Routes>
            <Route exact path="/" element={<LogIn/>} />
            <Route exact path="/profile" element={<UserProfile/>} />
            <Route exact path="/certificates" element={<Certificates/>} />
            <Route exact path="/certificate/:serialNumber" element={<SingleCertificate/>} />
          </Routes>
        </div>
      </Router>
    </ToastProvider>
  );
}

export default App;
