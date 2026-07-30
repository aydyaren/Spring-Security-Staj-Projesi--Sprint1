import {
    Download,
    Pencil,
    Trash2
} from "lucide-react";

import documentService from "../../services/documentService";

import "./DocumentTable.css";

function DocumentTable({
                           documents,
                           currentUser,
                           onDelete,
                           onUpdate
                       }) {

    // Belgeyi indirir.
    const handleDownload = async (id, fileName) => {

        try {

            const response = await documentService.downloadDocument(id);

            const url = window.URL.createObjectURL(
                new Blob([response.data])
            );

            const link = document.createElement("a");

            link.href = url;
            link.download = fileName;

            document.body.appendChild(link);

            link.click();

            link.remove();

            window.URL.revokeObjectURL(url);

        } catch (error) {

            console.error(error);

            alert("Download failed.");

        }

    };

    if (documents.length === 0) {

        return (

            <div className="alert alert-light text-center p-5 rounded-4 shadow-sm">

                <h5>No documents found</h5>

                <p className="text-muted mb-0">

                    Upload your first document.

                </p>

            </div>

        );

    }

    return (

        <div className="document-table">

            <table className="table">

                <thead>

                <tr>

                    <th>Title</th>
                    <th>Description</th>
                    <th>File Name</th>
                    <th>Owner</th>
                    <th>Created At</th>
                    <th>Actions</th>

                </tr>

                </thead>

                <tbody>

                {

                    documents.map((document) => {

                        const isAdmin =
                            currentUser?.role === "ADMIN";

                        const isOwner =
                            currentUser?.username === document.ownerUsername;

                        return (

                            <tr key={document.id}>

                                <td title={document.title}>

                                    {document.title}

                                </td>

                                <td title={document.description}>

                                    {document.description}

                                </td>

                                <td title={document.fileName}>

                                    {document.fileName}

                                </td>

                                <td title={document.ownerUsername}>

                                    {document.ownerUsername}

                                </td>

                                <td>

                                    {

                                        new Date(
                                            document.createdAt
                                        ).toLocaleString("tr-TR")

                                    }

                                </td>

                                <td>

                                    {

                                        (isAdmin || isOwner) && (

                                            <div className="action-buttons">

                                                <button
                                                    className="download-btn"
                                                    title="Download"
                                                    onClick={() =>
                                                        handleDownload(
                                                            document.id,
                                                            document.fileName
                                                        )
                                                    }
                                                >

                                                    <Download size={18}/>

                                                </button>

                                                <button
                                                    className="update-btn"
                                                    title="Update"
                                                    onClick={() =>
                                                        onUpdate(document)
                                                    }
                                                >

                                                    <Pencil size={18}/>

                                                </button>

                                                <button
                                                    className="delete-btn"
                                                    title="Delete"
                                                    onClick={() =>
                                                        onDelete(document.id)
                                                    }
                                                >

                                                    <Trash2 size={18}/>

                                                </button>

                                            </div>

                                        )

                                    }

                                </td>

                            </tr>

                        );

                    })

                }

                </tbody>

            </table>

        </div>

    );

}

export default DocumentTable;