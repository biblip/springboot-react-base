 import React, { useEffect, useState } from 'react'
 import { Container, Typography, Button } from '@mui/material'

 function App() {
   const [message, setMessage] = useState('')

   const fetchMessage = async () => {
     try {
       const res = await fetch('/api/hello')
       const text = await res.text()
       setMessage(text)
     } catch (err) {
       setMessage('Error fetching message')
     }
   }

   useEffect(() => {
     fetchMessage()
   }, [])

   return (
     <Container sx={{ mt: 4 }}>
       <Typography variant="h4" gutterBottom>
         {message || 'Loading...'}
       </Typography>
       <Button variant="contained" onClick={fetchMessage}>
         Refresh
       </Button>
     </Container>
   )
 }

 export default App