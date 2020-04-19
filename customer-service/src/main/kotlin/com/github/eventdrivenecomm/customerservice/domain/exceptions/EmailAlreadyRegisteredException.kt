package com.github.eventdrivenecomm.customerservice.domain.exceptions

import java.lang.RuntimeException

class EmailAlreadyRegisteredException : RuntimeException("This e-mail has already been registered")