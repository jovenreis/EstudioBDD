import { NavLink, Route, Routes } from 'react-router-dom'
import SeletorUsuario from './components/SeletorUsuario.jsx'
import ListaProjetos from './components/ListaProjetos.jsx'
import Inicio from './pages/Inicio.jsx'
import ProjetoPagina from './pages/ProjetoPagina.jsx'

export default function App() {
  return (
    <div className="app">
      <header className="topo">
        <NavLink to="/" className="marca">Estúdio BDD</NavLink>
        <SeletorUsuario />
      </header>

      <div className="corpo">
        <aside className="lateral">
          <ListaProjetos />
        </aside>

        <main className="conteudo">
          <Routes>
            <Route path="/" element={<Inicio />} />
            <Route path="/projetos/:id" element={<ProjetoPagina />} />
            <Route path="*" element={<p className="aviso">Página não encontrada.</p>} />
          </Routes>
        </main>
      </div>
    </div>
  )
}
