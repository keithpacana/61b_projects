# Targets that don't correspond to files, but are to be treated as commands.
<<<<<<< f4f8092387c7cbd4d227cb0d0fa0f889c3dd4b25
.PHONY: default check clean output

CPATH = "..:$(CLASSPATH):;..;$(CLASSPATH)"

default: check

check: 
	@echo "Testing correct inputs..."
	@CLASSPATH=$(CPATH) bash test-correct correct/*.inp
	@echo
	@echo "Testing erroneous inputs..."
	@CLASSPATH=$(CPATH) bash test-error error/*.inp

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(RM) *~ OUT* ERRS*
=======
.PHONY: default check clean 

SCRIPT = test-ataxx.py

CPATH = "..:$(CLASSPATH):;..;$(CLASSPATH)"

# The Python interpreter (default value). Settable on the command line.
# If your system calls python3 by a different name (e.g., python), then
# you can indicate this by invoking any of the makefiles with
#        make PYTHON=python check
PYTHON = python3

default: check

check: 
	@if [ -f $(SCRIPT) ]; then \
	    echo 'CLASSPATH=$(CPATH) $(PYTHON) $(SCRIPT) *.inp'; \
	    CLASSPATH=$(CPATH) $(PYTHON) $(SCRIPT) *.inp; \
	else \
	    echo; \
	    echo "=========================="; \
	    echo "= No integration tester. ="; \
	    echo "=========================="; \
	    echo; \
	    true; \
	fi
	@echo

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(RM) *~ *.tmp

>>>>>>> proj2 ataxx
