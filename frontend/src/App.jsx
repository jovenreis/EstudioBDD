import { NavLink, Route, Routes } from 'react-router-dom'
import UsuarioLogado from './components/UsuarioLogado.jsx'
import ListaProjetos from './components/ListaProjetos.jsx'
import Inicio from './pages/Inicio.jsx'
import ProjetoPagina from './pages/ProjetoPagina.jsx'
import HistoriaPagina from './pages/HistoriaPagina.jsx'
import HistoriaForm from './pages/HistoriaForm.jsx'
import ResumoPagina from './pages/ResumoPagina.jsx'
import LoginPagina from './pages/LoginPagina.jsx'
import QuadroPagina from './pages/QuadroPagina.jsx'
import { useUsuario } from './UsuarioContext.jsx'

export default function App() {
  const { usuario } = useUsuario()

  // sem login, só a tela de entrada aparece
  if (!usuario) return <LoginPagina />

  return (
    <div className="app">
      <header className="topo">
        <NavLink to="/" className="marca">Estúdio BDD</NavLink>
        <UsuarioLogado />
      </header>

      <div className="corpo">
        <aside className="lateral">
          <ListaProjetos />
        </aside>

        <main className="conteudo">
          <Routes>
            <Route path="/" element={<Inicio />} />
            <Route path="/projetos/:id" element={<ProjetoPagina />} />
            <Route path="/projetos/:id/resumo" element={<ResumoPagina />} />
            <Route path="/projetos/:id/quadro" element={<QuadroPagina />} />
            <Route path="/projetos/:projetoId/historias/nova" element={<HistoriaForm />} />
            <Route path="/historias/:id" element={<HistoriaPagina />} />
            <Route path="/historias/:id/editar" element={<HistoriaForm />} />
            <Route path="*" element={<p className="aviso">Página não encontrada.</p>} />
          </Routes>
        </main>
      </div>
    </div>
  )
}
