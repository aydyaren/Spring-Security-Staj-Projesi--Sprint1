import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

import { resetSessionExpired } from "../services/api";

import "./SessionExpiredModal.css";

function SessionExpiredModal({

                                 open,

                                 onClose

                             }) {

    const navigate = useNavigate();

    /*
     * Modal açıkken:
     * - Sayfanın scroll'unu kapatır.
     * - ESC ile modalı kapatır.
     */
    useEffect(() => {

        if (!open) {

            document.body.style.overflow = "auto";

            return;

        }

        document.body.style.overflow = "hidden";

        const handleEsc = (event) => {

            if (event.key === "Escape") {

                handleOk();

            }

        };

        window.addEventListener("keydown", handleEsc);

        return () => {

            document.body.style.overflow = "auto";

            window.removeEventListener("keydown", handleEsc);

        };

    }, [open]);

    /*
     * Kullanıcı OK butonuna bastığında:
     * - Modal kapanır.
     * - Session flag sıfırlanır.
     * - Login sayfasına yönlendirilir.
     */
    const handleOk = () => {

        resetSessionExpired();

        onClose();

        navigate("/");

    };

    if (!open) {

        return null;

    }

    return (

        <div
            className="session-modal-overlay"
            onClick={handleOk}
        >

            <div
                className="session-modal"
                onClick={(event) => event.stopPropagation()}
            >

                {/* Header */}

                <div className="session-modal-header">

                    <h2>

                        Session Expired

                    </h2>

                    <p>

                        Your session has expired.

                        <br />

                        Please login again.

                    </p>

                </div>

                {/* Footer */}

                <div className="session-modal-footer">

                    <button
                        className="session-modal-button"
                        onClick={handleOk}
                    >

                        OK

                    </button>

                </div>

            </div>

        </div>

    );

}

export default SessionExpiredModal;