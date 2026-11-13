// Mostra o erro devolvido pela API: a mensagem principal e, se houver, os campos inválidos.
export default function MensagemErro({ erro }) {
  if (!erro) return null
  const campos = Object.values(erro.erros ?? {})

  return (
    <div className="mensagem-erro" role="alert">
      <p>{erro.message}</p>
      {campos.length > 0 && (
        <ul>
          {campos.map((texto) => (
            <li key={texto}>{texto}</li>
          ))}
        </ul>
      )}
    </div>
  )
}
