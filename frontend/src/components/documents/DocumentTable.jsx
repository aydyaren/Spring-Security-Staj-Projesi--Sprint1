import documentService from "../../services/documentService";

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

            <div className="alert alert-info">

                No documents found.

            </div>

        );

    }

    return (

        <table className="table table-bordered table-hover">

            <thead className="table-dark">

            <tr>

                <th>ID</th>
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

                            <td>{document.id}</td>

                            <td>{document.title}</td>

                            <td>{document.description}</td>

                            <td>{document.fileName}</td>

                            <td>{document.ownerUsername}</td>

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

                                        <>

                                            <button
                                                className="btn btn-primary btn-sm me-2"
                                                onClick={() =>
                                                    handleDownload(
                                                        document.id,
                                                        document.fileName
                                                    )
                                                }
                                            >

                                                Download

                                            </button>

                                            <button
                                                className="btn btn-warning btn-sm me-2"
                                                onClick={() =>
                                                    onUpdate?.(document)
                                                }
                                            >

                                                Update

                                            </button>

                                            <button
                                                className="btn btn-danger btn-sm"
                                                onClick={() =>
                                                    onDelete(document.id)
                                                }
                                            >

                                                Delete

                                            </button>

                                        </>

                                    )

                                }

                            </td>

                        </tr>

                    );

                })

            }

            </tbody>

        </table>

    );

}

export default DocumentTable;