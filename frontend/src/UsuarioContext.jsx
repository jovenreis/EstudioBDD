import { createContext, useContext, useState } from 'react'
import { api, definirUsuario } from './api.js'

// Guarda quem fez login. O id vai no cabeçalho X-Usuario-Id das requisições.
// A senha é conferida só no login (POST /api/login); não existe token.

const UsuarioContext = createContext(null)
const CHAVE = 'estudio-bdd:usuario'

function lerSalvo() {
  try {
    const salvo = JSON.parse(localStorage.getItem(CHAVE))
    return salvo?.id ? salvo : null
  } catch {
    return null
  }
}

function salvar(usuario) {
  try {
    if (usuario) localStorage.setItem(CHAVE, JSON.stringify(usuario))
    else localStorage.removeItem(CHAVE)
  } catch {
    // sem armazenamento no navegador, o login vale só até recarregar a página
  }
}

export function UsuarioProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    const salvo = lerSalvo()
    definirUsuario(salvo?.id ?? null)
    return salvo
  })

  async function entrar(email, senha) {
    const logado = await api.login(email, senha)
    definirUsuario(logado.id)
    salvar(logado)
    setUsuario(logado)
  }

  function sair() {
    definirUsuario(null)
    salvar(null)
    setUsuario(null)
  }

  return (
    <UsuarioContext.Provider value={{ usuario, entrar, sair }}>
      {children}
    </UsuarioContext.Provider>
  )
}

export function useUsuario() {
  return useContext(UsuarioContext)
}
