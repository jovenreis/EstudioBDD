import { createContext, useContext, useEffect, useState } from 'react'
import { api, definirUsuario } from './api.js'

// Guarda o usuário de demonstração escolhido. Ele vai no cabeçalho X-Usuario-Id
// de todas as requisições (identidade simulada, não é login).

const UsuarioContext = createContext(null)
const CHAVE = 'estudio-bdd:usuario'

function lerSalvo() {
  try {
    return Number(localStorage.getItem(CHAVE)) || null
  } catch {
    return null
  }
}

export function UsuarioProvider({ children }) {
  const [usuarios, setUsuarios] = useState([])
  const [usuarioId, setUsuarioId] = useState(lerSalvo)
  const [erro, setErro] = useState(null)

  useEffect(() => {
    api.usuarios()
      .then((lista) => {
        setUsuarios(lista)
        // se o usuário salvo não existe mais, começa pelo primeiro da lista
        setUsuarioId((atual) => (lista.some((u) => u.id === atual) ? atual : lista[0]?.id ?? null))
      })
      .catch((e) => setErro(e.message))
  }, [])

  useEffect(() => {
    definirUsuario(usuarioId)
    try {
      if (usuarioId) localStorage.setItem(CHAVE, String(usuarioId))
    } catch {
      // sem armazenamento no navegador a escolha vale só nesta aba
    }
  }, [usuarioId])

  const usuario = usuarios.find((u) => u.id === usuarioId) ?? null

  return (
    <UsuarioContext.Provider value={{ usuarios, usuario, setUsuarioId, erro }}>
      {children}
    </UsuarioContext.Provider>
  )
}

export function useUsuario() {
  return useContext(UsuarioContext)
}
