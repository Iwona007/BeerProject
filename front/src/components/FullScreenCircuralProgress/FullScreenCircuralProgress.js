import React from 'react'
import { connect } from 'react-redux'

import { CircularProgress } from '@material-ui/core';

const styles = {
  div: {
    position: 'fixed',
    width: '100vw',
    height: '100vh',
    top: 0,
    left: 0,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
    backgroundColor: 'grey',
    opacity: 0.8
  }
}

const FullScreenCircuralProgress = props => {
  return (
    props._isOpen ?
      <div style={styles.div}>
        <CircularProgress size={80} />
      </div>
      :
      null
  )
}

const mapStateToProps = state => ({
  _isOpen: state.fullScreenCircuralProgress.fetchs.length
})

export default connect(
  mapStateToProps
)(FullScreenCircuralProgress)